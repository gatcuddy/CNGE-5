package game;

import cnge.core.*;
import cnge.core.interfaces.LoadSubLooper;
import cnge.core.interfaces.SubLooper;
import cnge.graphics.FrameBuffer;
import cnge.graphics.Shader;
import cnge.graphics.Window;
import cnge.graphics.shape.RectShape;
import cnge.graphics.texture.Texture;
import cnge.graphics.texture.TexturePreset;
import game.shaders.ColorShader;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;

public class Main extends CNGE {

	/*
	 * these ones are for the load screens so they don't have to load themselves
	 */
	public static ColorShader colorShader;
	public static RectShape rect;

	private BaseShader baseShader;

	protected Main() {
		TexturePreset.setDefaults(GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR);

		initGameSize(256, 144);
		initScreenMode(Screen.makeAspectScreen(), -1);
		initWindow(new Window().initFull(false).initName("here we go").initIcon("res/icon.png").init());
		initLoopers(this::updateRender, this::loadRender);
		initAssetBundles(new GameAssets(), new MenuAssets(), new SharedAssets());
		initDebug(true);

		colorShader = new ColorShader();
		baseShader = new BaseShader();
		rect = new RectShape();

		initLoadScreens(new GameLoadScreen(colorShader, rect, camera), new MenuLoadScreen(colorShader, rect, camera));

		initGameLoop();
		setScene(new GameScene());
		gameStart();
	}

	private void updateRender(SubLooper update, SubLooper render) {
		update.subLoop();
		midRender();
		render.subLoop();
		postRender();
	}

	public void loadRender(LoadSubLooper render, int along, int total) {
		CNGE.window.pollEvents();
		midRender();
		render.subLoop(along, total);
		postRender();
		CNGE.window.swap();
	}

	public void midRender() {
		CNGE.camera.update();
		CNGE.gameBuffer.enableTexture();
		Window.clear(1, 0, 0, 1);
	}

	public void postRender() {
		FrameBuffer.enableDefault();
		Window.clear(0, 0f, 0f, 1);

		baseShader.enable();
		baseShader.setMvp(CNGE.camera.ndcFullMatrix());
		CNGE.screen.setScreenViewport();

		rect.render();

		Shader.disable();
		Texture.unbind();
	}

}
