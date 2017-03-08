package chrislo27.rhre

import com.badlogic.gdx.utils.Align
import ionium.registry.AssetRegistry
import ionium.util.i18n.Localization

abstract class NewUIScreen(m: Main) : BackgroundedScreen(m) {

	val BG_WIDTH = main.camera.viewportWidth * 0.85f
	val BG_HEIGHT = main.camera.viewportHeight * 0.9f
	val TITLE_SCALE: Float = 0.75f
	val PADDING = 32f
	val ICON_SIZE = main.biggerFont.capHeight * TITLE_SCALE

	abstract var icon: String
	abstract var title: String
	abstract var bottomInstructions: String

	override fun render(delta: Float) {
		super.render(delta)

		main.batch.begin()
		main.batch.setColor(0f, 0f, 0f, 0.65f)

		val startX = main.camera.viewportWidth * 0.5f - BG_WIDTH * 0.5f
		val startY = main.camera.viewportHeight * 0.5f - BG_HEIGHT * 0.5f

		ionium.templates.Main.fillRect(main.batch, startX, startY, BG_WIDTH, BG_HEIGHT)
		main.batch.setColor(1f, 1f, 1f, 1f)

		main.batch.draw(AssetRegistry.getTexture(icon), startX + PADDING, startY + BG_HEIGHT - PADDING - ICON_SIZE,
						ICON_SIZE, ICON_SIZE)
		main.biggerFont.data.setScale(TITLE_SCALE)
		main.biggerFont.setColor(1f, 1f, 1f, 1f)
		Main.drawCompressed(main.biggerFont, main.batch, Localization.get(title), startX + PADDING * 1.5f + ICON_SIZE,
							startY + BG_HEIGHT - PADDING, BG_WIDTH - PADDING * 2.5f - ICON_SIZE, Align.left)

		ionium.templates.Main.fillRect(main.batch, startX + PADDING,
									   startY + BG_HEIGHT - PADDING - main.biggerFont.capHeight - PADDING,
									   BG_WIDTH - PADDING * 2, 3f)
		main.biggerFont.data.setScale(1f)
		main.font.setColor(1f, 1f, 1f, 1f)
		val instructionsStart = startY + main.font.capHeight * 4f
		Main.drawCompressed(main.font, main.batch, Localization.get(bottomInstructions), startX + PADDING,
							instructionsStart, BG_WIDTH - PADDING * 2, Align.center)

		ionium.templates.Main.fillRect(main.batch, startX + PADDING,
									   instructionsStart + PADDING * 0.5f,
									   BG_WIDTH - PADDING * 2, 3f)

		main.batch.end()
	}

}
