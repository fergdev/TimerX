package com.timerx.kompare

import kotlinx.serialization.Serializable

@Serializable
data class KNode(
    val id: Int,
    val bounds: Rect,
    val properties: Map<String, String>,
    val children: List<KNode> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (other !is KNode) return false
        if (bounds != other.bounds) return false
        if (properties != other.properties) return false
        if (children != other.children) return false
        return true
    }

    override fun hashCode(): Int {
        var result = bounds.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }
}
