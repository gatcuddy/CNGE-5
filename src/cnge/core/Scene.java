package cnge.core;

import cnge.graphics.Transform;

public abstract class Scene extends CNGE {

	//class to wrap this so these can passed by reference
	class LoadStruct {
		public LoadScreen loadScreen;
		public int along;
		public int total;

		public LoadStruct(LoadScreen ls, int t) {
			loadScreen = ls;
			along = 0;
			total = t;
		}
	}

	public static class Shared extends Scene {
		public Shared(){super(null, null);}
		public void sceneStart(){}
		public void render(){}
		public void update(){}
	}

	/**
	 * loads all required assets for the scene,
	 * then starts the scene
	 *
	 * @param unloadFlags - pass this in each time you construct a class on each time a new scene is loaded, which assetbundles to unload
	 * @param loadScreen - the class of the loadscreen for this scene, gotten from CNGE
	 * @param sceneDependecies - classes for asset bundles this scene depends on, gotten from CNGE
	 */
	public Scene(Class<? extends AssetBundle>[] unloadFlags, Class<? extends LoadScreen> loadScreen, Class<? extends AssetBundle>... sceneDependecies) {

		//how many assets will be loaded in total;
		int total = 0;

		int allBundles = assetBundles.length;

		int maxUnloads = unloadFlags.length;
		int maxLoads = sceneDependecies.length;

		//to fill our unload list
		int unloadAlong = 0;
		//our list of bundles to actually unload
		AssetBundle[] unloads = new AssetBundle[maxUnloads];
		//get all assetbundles that match the classes of the ones we need to unload
		//and make sure they're actually loaded
		for(int i = 0; i < maxUnloads; ++i) {
			Class<? extends AssetBundle> bundleClass = sceneDependecies[i];
			for(int j = 0; j < allBundles; ++j) {
				AssetBundle ab = assetBundles[j];
				if(bundleClass == ab.getClass() && ab.isLoaded()) {
					total += ab.getTotal();
					unloads[unloadAlong++] = ab;
				}
			}
		}

		//to fill our load list
		int loadAlong = 0;
		//our list of bundles to actually load
		AssetBundle[] loads = new AssetBundle[maxLoads];
		//get all assetbundles that match the classes of the ones we need to load
		//and make sure they're actually now loaded
		for(int i = 0; i < maxLoads; ++i) {
			Class<? extends AssetBundle> bundleClass = sceneDependecies[i];
			for(int j = 0; j < allBundles; ++j) {
				AssetBundle ab = assetBundles[j];
				if(bundleClass == ab.getClass() && !ab.isLoaded()) {
					total += ab.getTotal();
					loads[loadAlong++] = ab;
				}
			}
		}

		LoadScreen lsInstance = null;

		//get our loadscreen
		int allLoadScreens = loadScreens.length;
		for(int i = 0; i < allLoadScreens; ++i) {
			LoadScreen tls = loadScreens[i];
			if(tls.getClass() == loadScreen) {
				lsInstance = tls;
			}
		}
		LoadStruct loadStruct = new LoadStruct(lsInstance, total);

		//give the assetbundles information to work with
		AssetBundle.setup(loadStruct);

		//now actually unload the unloads
		for(int i = 0; i < unloadAlong; ++i) {
			unloads[i].unload(this);
		}
		//now actually load the dependencies
		for(int i = 0; i < loadAlong; ++i) {
			loads[i].load(this);
		}

		//start the scene fina
		sceneStart();
	}
	
	public void setCameraCenter(float x, float y) {
		Transform ct = camera.getTransform();
		ct.setTranslation(x - ct.getWidth() / 2, y - ct.getHeight() / 2);
	}
	
	public float ccx(float x, float w) {
		Transform ct = camera.getTransform();
		return x - (ct.getWidth() / 2) + (w / 2);
	}
	
	public float ccy(float y, float h) {
		Transform ct = camera.getTransform();
		return y - (ct.getHeight() / 2) + (h / 2);
	}
	
	/*
	 * fun stuff to do with entities
	 */
	
	public void createEntity(Entity e, float x, float y) {
		e.setup(x, y);
	}
	
	public void eUpdate(Entity e) {
		e.update();
	}
	
	public void eUpdate_S(Entity e) {
		if(e != null) {
			e.update();
		}
	}
	
	public void eUpdate_OS(Entity e) {
		if(e != null) {
			e.onScreenUpdate();
			if(e.onScreen) {
				e.update();
			}
		} 
	}
	
	public void eUpdate_O(Entity e) {
		e.onScreenUpdate();
		if(e.onScreen) {
			e.update();
		}
	}

	/*
	 * fun rendering stuff to do with entities
	 */
	
	public void eRender(Entity e) {
		e.render();
	}
	
	/**
	 * renders an entity, checking whether its on screen.
	 * USE FOR ENTITIES THAT WILL NEVER BE NULL
	 * 
	 * @param e - the entity to loadRender
	 */
	public void eRender_O(Entity e) {
		if(e.onScreen) {
			e.render();
		}
	}
	
	/**
	 * 
	 */
	public void eRender_S(Entity e) {
		if(e != null) {
			e.render();
		}
	}
	
	/**
	 * renders an entity, but checks if it's null first
	 * 
	 * @param e - the entity to loadRender
	 */
	public void eRender_OS(Entity e) {
		if(e != null && e.onScreen) {
			e.render();
		}
	}
	
	/*
	 * override this stuff to do stuff with the scene
	 * VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
	 */

	/**
	 * optional if you want to override this
	 * @param w - the width of the game area onscreen will be passed here
	 * @param h - the height of the game area onscreen will be passed here
	 */
	public void windowReszied(int w, int h) {}

	/**
	 * will be called before the scene starts, does loads that will still have the load screen on
	 */
	abstract public void sceneStart();

	abstract public void render();
	
	abstract public void update();
	
}