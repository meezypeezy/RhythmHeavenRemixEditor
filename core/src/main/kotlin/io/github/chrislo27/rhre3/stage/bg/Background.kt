package io.github.chrislo27.rhre3.stage.bg

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.github.chrislo27.toolboks.registry.AssetRegistry


abstract class Background(val id: String) {
    
    abstract fun render(camera: OrthographicCamera, batch: SpriteBatch, shapeRenderer: ShapeRenderer, delta: Float)
    
    companion object {
        data class BgData(val bg: Background, val name: String)
        
        private val kmStripes2: BgData = BgData(KarateManStripesBackground("karateManStripes2"), "Karate Man GBA 2")
        
        val backgroundsData: List<BgData> by lazy {
            listOf(
                    BgData(TengokuBackground("tengoku"), "GBA Game Select"),
                    BgData(KarateManBackground("karateMan"), "Karate Man"),
                    BgData(TilingBackground("rhdsPolkaDots", 5f,
                                            speedX = 3f, speedY = -3f, widthCoeff = 0.5f, heightCoeff = 0.5f)
                           { AssetRegistry["bg_polkadot"] }, "DS Game Select"),
                    BgData(SpaceDanceBackground("spaceDance"), "Space Dance"),
                    BgData(RetroBackground("retro"), "Retro"),
                    BgData(TilingBackground("tapTrial", 5f, speedX = 0f, speedY = 1f) { AssetRegistry["bg_tapTrial"] }, "Tap Trial"),
                    BgData(TilingBackground("tiled", 5f, speedX = 1f, speedY = 1f) { AssetRegistry["bg_tile"] }, "Notes"),
                    BgData(LaunchPartyBackground("launchParty"), "Launch Party"),
                    BgData(KittiesBackground("kitties"), "Kitties!"),
                    BgData(SeesawBackground("seesaw"), "See-Saw"),
                    BgData(KarateManStripesBackground("karateManStripes1", stripe1 = Color.valueOf("FEC652"),
                                                      stripe2 = Color.valueOf("FFE86C")), "Karate Man GBA"),
                    kmStripes2,
                    BgData(BTSDSBackground("btsDS"), "Built to Scale DS"),
                    BgData(BTSDSBackground("btsDS2", Color.valueOf("E1E11FFF")), "Built to Scale DS 2"),
                    BgData(BTSDSBackground("btsDSBlue", Color.valueOf("2963FFFF")), "Built to Scale DS (Blue)")
//                    BgData(PolyrhythmBackground("polyrhythm"), "Polyrhythm")
                  )
        }
        val backgrounds: List<Background> by lazy { backgroundsData.map { it.bg } }
        val backgroundMap: Map<String, Background> by lazy { backgrounds.associateBy(Background::id) }
        val backgroundMapByBg: Map<Background, BgData> by lazy { backgroundsData.associateBy { it.bg } }
        val defaultBackground: Background
            get() = backgroundMap.getValue("tengoku")
    }
    
}