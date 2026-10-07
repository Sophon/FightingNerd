package io.github.sophon.fightingnerd.adapter.outbound.revenueCat

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.PurchasesTransactionException
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption
import io.github.sophon.fightingnerd.app.outPort.TipPort

@ExcludeFromCoverage("TODO: needs a RevenueCat fake")
internal class RevenueCatAdapter: TipPort {
    override suspend fun getTipOptions(): Result<List<TipOption>, AppError> {
        val result = getPackageList().map { packageList ->
            val tipOptionList = packageList.map { rcPackage -> rcPackage.toTipOption() }
            tipOptionList
        }
        return result
    }

    /**
     * Re-fetches offerings to find the package - the SDK caches them in memory, so this doesn't hit the network.
     */
    override suspend fun purchase(tipOptionId: String): EmptyResult<AppError> {
        val result = getPackageList().flatMap { packageList ->
            val rcPackage = packageList.find { rcPackage -> rcPackage.identifier == tipOptionId }
            if (rcPackage == null) {
                Result.Error(AppError.PaymentError("Tip option not found: $tipOptionId"))
            } else {
                purchase(rcPackage)
            }
        }
        return result
    }


    private suspend fun getPackageList(): Result<List<Package>, AppError> {
        val result = try {
            val current = Purchases.sharedInstance.awaitOfferings().current
            if (current == null) {
                Result.Error(AppError.PaymentError(ERROR_NO_CURRENT_OFFERING))
            } else {
                Result.Success(current.availablePackages)
            }
        } catch (e: PurchasesException) {
            Result.Error(AppError.PaymentError(e.message))
        }
        return result
    }

    private suspend fun purchase(rcPackage: Package): EmptyResult<AppError> {
        val result = try {
            Purchases.sharedInstance.awaitPurchase(rcPackage)
            Result.Success(Unit)
        } catch (e: PurchasesTransactionException) {
            val error = if (e.code == PurchasesErrorCode.PurchaseCancelledError) {
                AppError.PurchaseCancelled
            } else {
                AppError.PaymentError(e.message)
            }
            Result.Error(error)
        } catch (e: PurchasesException) {
            Result.Error(AppError.PaymentError(e.message))
        }
        return result
    }
}


private fun Package.toTipOption(): TipOption {
    val tipOption = TipOption(
        id = identifier,
        title = storeProduct.title,
        formattedPrice = storeProduct.price.formatted,
    )
    return tipOption
}

private const val ERROR_NO_CURRENT_OFFERING = "No current offering"
