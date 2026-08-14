package edu.metrostate.ics342.mediatracker.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MediaTypeSerializer::class)
enum class MediaType(val apiString: String) {
    BOOK("book"),
    MOVIE("movie"),
    SHOW("show"),
    UNKNOWN("unknown");

    val displayName: String
        get() = apiString.replaceFirstChar { it.uppercase() }
}

object MediaTypeSerializer : KSerializer<MediaType> {
    override val descriptor =
        PrimitiveSerialDescriptor("MediaType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: MediaType) {
        encoder.encodeString(value.apiString)
    }

    override fun deserialize(decoder: Decoder): MediaType {
        val raw = decoder.decodeString().trim().lowercase()
        return MediaType.entries.firstOrNull { it.apiString == raw }
            ?: MediaType.UNKNOWN
    }
}