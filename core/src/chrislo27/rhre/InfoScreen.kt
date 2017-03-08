package chrislo27.rhre

import chrislo27.rhre.registry.Game
import chrislo27.rhre.registry.GameRegistry
import chrislo27.rhre.version.VersionChecker
import chrislo27.rhre.version.VersionState
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import ionium.registry.AssetRegistry
import ionium.registry.ScreenRegistry
import ionium.screen.Updateable
import ionium.util.DebugSetting
import ionium.util.MathHelper
import ionium.util.Utils
import ionium.util.i18n.Localization
import ionium.util.render.TexturedQuad

class InfoScreen(m: Main) : Updateable<Main>(m) {

	// 日本語 (グーグル翻訳) (Japanese [G. Translate]): Whistler_420, Google Translate

	private val sections: Map<String, String> = mapOf(
			"programming" to "chrislo27",
			"databasing" to "ahemtoday, Huebird of Happiness, GuardedLolz, chrislo27",
			"localization" to """English (UK): Whistler_420
Español (Spanish): Killble, quantic, GlitchyPSIX, David Mismo
Français (French): Gabgab2222, Pengu12345, Lovestep, Dragoneteur
Italiano (Italian): Huebird of Happiness""",
			"sfx" to "F Yeah, Rhythm Heaven! Tumblr, ahemtoday, Haydorf, megaminerzero, Chocolate2890, Whistler_420, TieSoul, Huebird of Happiness, GuardedLolz, TheRhythmKid, Mariofan5000",
			"icons" to "ahemtoday, Whistler_420, Killble, TheNewOrchestra, Altonotone, Pengu12345, fartiliumstation, TheRhythmKid, Chowder",
			"uidesign" to "GlitchyPSIX",
			"misc" to "Pengu12345, ToonLucas22, Strawzzboy64",
			"technologies" to "[DARK_GRAY]Lib[][#E10000]GDX[] by Badlogic Games, LWJGL\n[#B07219]Java[] by Oracle, [#FF8900]Kotlin[] by JetBrains\nRhythm Heaven assets by Nintendo\n\nYou"
													 )
	private var concatSections: String = ""

	fun createConcatSections() {
		concatSections = sections.map {
			"[LIGHT_GRAY]" + Localization.get("info.credits." + it.key).toUpperCase(
					Localization.instance().currentBundle.locale.locale) + "[]\n" + it.value
		}.joinToString(separator = "\n\n")
	}

	private val patternCount: Int by lazy {
		GameRegistry.instance().gameList.flatMap(Game::patterns).filter { !it.autoGenerated }.count()
	}

	private val soundCueCount: Int by lazy {
		GameRegistry.instance().gameList.map { it.soundCues.size }.sum()
	}

	override fun render(delta: Float) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		main.batch.begin()

		if (DebugSetting.debug) {
			val tex: Texture = AssetRegistry.getTexture("ptr_whole")
			val originX = main.camera.viewportWidth * 0.5f - tex.width * 0.5f
			val originY = 128f
			val adjust = (MathHelper.getTriangleWave(0.75f) - 0.5f) / 0.5f
			// hat
			// 59, 421, 186, 133
			TexturedQuad.renderQuad(main.batch, tex,
									originX + 59, originY + 288,
									originX + 59 + 186, originY + 288,
									originX + 59 + 186 + adjust * 16, originY + 288 + 133,
									originX + 59 + adjust * 16, originY + 288 + 133,
									(59f) / tex.width, (5f) / tex.height,
									(59f + 186f) / tex.width, (133f) / tex.height)
			TexturedQuad.renderQuad(main.batch, tex,
									originX, originY,
									originX + tex.width, originY,
									originX + tex.width, originY + tex.height - 133,
									originX, originY + tex.height - 133,
									0f, (5f + 133f) / tex.height,
									1f, 1f)
		}

		val url = "https://github.com/chrislo27/RhythmHeavenRemixEditor2"
		val urlLength = Utils.getWidth(main.font, url)
		val hoveringOverUrl = main.getInputY() <= main.font.lineHeight * 1.25f * main.camera.zoom &&
				main.getInputX() >= main.camera.viewportWidth * 0.5f - urlLength * 0.5f &&
				main.getInputX() <= main.camera.viewportWidth * 0.5f + urlLength * 0.5f
		main.font.setColor(0.5f, 0.65f, 1f, 1f)
		if (hoveringOverUrl) {
			main.font.setColor(0.6f, 0.75f, 1f, 1f)
		}
		main.font.draw(main.batch, url,
					   main.camera.viewportWidth * 0.5f,
					   main.camera.viewportHeight - main.font.capHeight, 0f, Align.center, false)
		main.font.draw(main.batch, "_________________________________________________________",
					   main.camera.viewportWidth * 0.5f,
					   main.camera.viewportHeight - main.font.capHeight, 0f, Align.center, false)

		main.font.setColor(1f, 1f, 1f, 1f)

		main.font.data.setScale(0.65f)

		var height: Float = Utils.getHeightWithWrapping(main.font, concatSections,
														main.camera.viewportWidth * 0.45f)

		main.font.draw(main.batch, concatSections,
					   main.camera.viewportWidth * 0.025f,
					   main.camera.viewportHeight * 0.5f + height * 0.5f, main.camera.viewportWidth * 0.45f,
					   Align.topLeft, true)
		main.font.data.setScale(1f)

		val stats: String = Localization.get("info.stats", "${GameRegistry.instance().gameList.size}", "$patternCount",
											 "$soundCueCount")
		height = Utils.getHeightWithWrapping(main.font, stats,
											 main.camera.viewportWidth * 0.45f)

		main.font.draw(main.batch, stats,
					   main.camera.viewportWidth * 0.525f,
					   main.camera.viewportHeight * 0.75f + height * 0.5f, main.camera.viewportWidth * 0.45f,
					   Align.center, true)

		main.font.data.setScale(0.75f)
		val license: String = Localization.get("info.credits.license")
		height = Utils.getHeightWithWrapping(main.font, license,
											 main.camera.viewportWidth * 0.45f)

		main.font.draw(main.batch, license,
					   main.camera.viewportWidth * 0.525f,
					   main.camera.viewportHeight * 0.25f + height * 0.5f, main.camera.viewportWidth * 0.45f,
					   Align.right, true)

		main.font.data.setScale(1f)

		if (Utils.isButtonJustPressed(Input.Buttons.LEFT) && hoveringOverUrl) {
			Gdx.net.openURI(url)
		}

		if (VersionChecker.versionState != VersionState.GETTING
				&& VersionChecker.versionState != VersionState.FAILED) {
			main.font.draw(main.batch, Localization.get("info.version"),
						   main.camera.viewportWidth * 0.025f,
						   main.font.capHeight * 2 + main.font.lineHeight, main.camera.viewportWidth * 0.95f,
						   Align.center,
						   true)
		}
		main.font.draw(main.batch, Localization.get("info.back"),
					   main.camera.viewportWidth * 0.025f,
					   main.font.capHeight * 2, main.camera.viewportWidth * 0.95f, Align.center, true)

		main.batch.end()
	}

	override fun renderUpdate() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			main.screen = ScreenRegistry.get("editor")
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {

			} else if (VersionChecker.versionState != VersionState.GETTING
					&& VersionChecker.versionState != VersionState.FAILED) {
				main.screen = ScreenRegistry.get("version")
			}
		}
	}

	override fun tickUpdate() {
	}

	override fun getDebugStrings(array: Array<String>?) {
	}

	override fun resize(width: Int, height: Int) {
	}

	override fun show() {
		createConcatSections()
	}

	override fun hide() {
	}

	override fun pause() {
	}

	override fun resume() {
	}

	override fun dispose() {
	}


}
