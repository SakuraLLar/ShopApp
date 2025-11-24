package dev.sakura.feature_profile.util

import dev.sakura.feature_profile.R

data class GradientColors(val start: Int, val center: Int, val end: Int)
object GradientBorderProvider {
    val coverToBorderColorMap = mapOf(
        R.drawable.cover_gradient_lava to GradientColors(R.color.lavaDark_cover, R.color.lava_cover, R.color.lavaLight_cover),
        R.drawable.cover_gradient_lavender to GradientColors(R.color.lavenderDark_cover, R.color.lavender_cover, R.color.lavenderLight_cover),
        R.drawable.cover_gradient_ocean to GradientColors(R.color.oceanDark_cover, R.color.ocean_cover, R.color.oceanLight_cover),
        R.drawable.cover_gradient_sunset to GradientColors(R.color.sunsetDark_cover, R.color.sunset_cover, R.color.sunsetLight_cover),
        R.drawable.cover_gradient_bitumen to GradientColors(R.color.bitumenDark_cover, R.color.bitumen_cover, R.color.bitumenLight_cover),
        R.drawable.cover_gradient_leaves to GradientColors(R.color.leavesDark_cover, R.color.leaves_cover, R.color.leavesLight_cover),
        R.drawable.cover_gradient_wine to GradientColors(R.color.wineDark_cover, R.color.wine_cover, R.color.wineLight_cover),
        R.drawable.cover_gradient_flowers to GradientColors(R.color.flowersDark_cover, R.color.flowers_cover, R.color.flowersLight_cover),
        R.drawable.cover_gradient_cloud to GradientColors(R.color.cloudDark_cover, R.color.cloud_cover, R.color.cloudLight_cover),
        R.drawable.cover_gradient_love to GradientColors(R.color.loveDark_cover, R.color.love_cover, R.color.loveLight_cover),
        R.drawable.cover_gradient_forest to GradientColors(R.color.forestDark_cover, R.color.forest_cover, R.color.forestLight_cover),
        R.drawable.cover_gradient_space to GradientColors(R.color.spaceDark_cover, R.color.space_cover, R.color.spaceLight_cover)
    )
}
