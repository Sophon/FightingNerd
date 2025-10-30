package com.example.wikiwavu.data

import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.network.safeCall
import com.example.wikiwavu.BASE_URL
import com.example.wikiwavu.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface WavuWikiDataSource {
    suspend fun fetchMoveList(char: String): Result<MoveListResponseDto, DataError.Remote>
}

internal class WavuWikiDataSourceImpl(
    private val httpClient: HttpClient,
): WavuWikiDataSource {
    override suspend fun fetchMoveList(char: String): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", "Move")
                parameter("where", "id LIKE '$char%'")
                parameter("order_by", "id")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
                parameter("fields", "id,name,input,parent,target,damage,startup,recv,tot,crush,block,hit,ch,notes,alias,image,video,alt,_pageNamespace=ns")
            }
        }
    }
}