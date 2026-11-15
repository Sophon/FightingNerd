package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Bucket serves a set BOOLEAN as `""` and omits an unset one - presence means `true`.
 */
internal object DragDownBucketBooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("BucketBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("BucketBooleanSerializer requires JSON")
        val flag = jsonDecoder.decodeJsonElement().jsonPrimitive.booleanOrNull ?: true
        return flag
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeBoolean(value)
    }
}
