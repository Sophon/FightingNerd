import domain.AdminFeatureInfo
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun adminModule() = module {
    single { AdminFeatureInfo }
    singleOf(::AdminToolImpl).bind<AdminTool>()
}