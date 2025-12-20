package io.github.sophon.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Author

internal class CreateReplyUseCase {
    fun invoke(query: String): Result<AdminResult, AdminError> {
        val queryFields = query.split(" ")
        if (queryFields.size < 2) return Result.Error(AdminError.WrongReplyFormat(query))

        val recipientFields = queryFields.first()
        val message = queryFields.drop(1).joinToString(" ")
        val username: String
        val id: String
        val channelId: String

        recipientFields.split("-").apply {
            if (size < 3) return Result.Error(AdminError.WrongReplyFormat(query))

            username = get(0)
            id = get(1)
            channelId = get(2)
        }
        val author = Author(username, id, channelId)

        return Result.Success(AdminResult(author, message))
    }
}
