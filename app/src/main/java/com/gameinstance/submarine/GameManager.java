package com.gameinstance.submarine;

import com.gameinstance.submarine.utils.TextureHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gringo on 11.12.2016 20:16.
 *
 */
public class GameManager {
    static Scene scene;
    static GameRenderer renderer;

    public static void initGame(final GameRenderer renderer) {
        scene = new Scene(renderer);
        GameManager.renderer = renderer;
        scene.addLayerSet("BackBuffer", Arrays.asList("landscape_back", "mobs_back"));
        scene.addLayerSet("Front", Arrays.asList("landscape", "submarines", "ships_and_tanks",
                "aircrafts", "hud"));
        Layer landscape_back = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("landscape_back", landscape_back);
        Layer mobs_back = new Layer(renderer.getProgramHandle("SimpleProgramHandle"));
        mobs_back.setColor(new float[] {1.0f, 0.0f, 0.0f, 0.5f });
        scene.addLayer("mobs_back", mobs_back);
        Layer landscape = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("landscape", landscape);
        Layer submarines = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("submarines", submarines);
        Layer shipsAndTanks = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("ships_and_tanks", shipsAndTanks);
        Layer aircrafts = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("aircrafts", aircrafts);
        Layer hud = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        hud.isGui = true;
        scene.addLayer("hud", hud);
        Primitive texPrimitive = renderer.createPrimitiveTextured();
        Sprite [] landscpB = createLandScape(R.drawable.background, 64, 1, texPrimitive);
        List<Sprite> landslistB = new ArrayList<>(Arrays.asList(landscpB));
        Sprite [] landscp = createLandScape(R.drawable.background, 64, 1, texPrimitive);
        List<Sprite> landslist = new ArrayList<>(Arrays.asList(landscp));
        Primitive colPrimitive = renderer.createPrimitiveColored();
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("SimpleProgramHandle"), colPrimitive);
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), texPrimitive);
        Sprite submarineBack = new Sprite(renderer, R.drawable.submarine, primitiveMap, 0.2f, 0.2f);
        landscape_back.addSprites(landslistB);
        mobs_back.addSprite(submarineBack);
        landscape.addSprites(landslist);
        submarines.addSprite(submarineBack);
        final Movable submarineMovable = new Movable(submarineBack);
        scene.addMovable(submarineMovable);
        InputController.addTouchHandler(new TouchHandler(1) {
            @Override
            public boolean touch(int x, int y) {
                submarineMovable.setTarget(renderer.convertCoords(x, y, false));
                return false;
            }

            @Override
            public boolean onDown(int x, int y) {
                return false;
            }

            @Override
            public boolean onUp(int x, int y) {
                return false;
            }
        });
        Button stopButton = new Button(renderer, new int [] {R.drawable.stop, R.drawable.stop1},
                primitiveMap, 0.5f, 0.5f, new Button.ClickListener() {
            @Override
            public void onClick() {
              submarineMovable.setMotionEnabled(false);
            }
        }, new float[] {1.5f, 0.75f});
        hud.addSprite(stopButton);
        renderer.setCameraTarget(submarineBack);
    }

    private static Sprite [] createLandScape(int textureId, int pixelsPerUnit, float unitSize, Primitive primitive) {
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int [] texHandles = TextureHelper.loadTexture2(renderer.getActivityContext(), textureId, pixelsPerUnit);
        int n = texHandles[0];
        int m = texHandles[1];
        Sprite [] sprites = new Sprite[m * n];
        float left = -(n * unitSize) / 2.0f + 0.5f * unitSize;
        float top = (m * unitSize) / 2.0f - 0.5f * unitSize;
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                sprites[j * n + i] = new Sprite(renderer, texHandles[j * n + i + 2], primitiveMap,
                        unitSize, new float[] {left + i * unitSize, top - j * unitSize});
            }
        }
        return sprites;
    }

    public static Scene getScene() {
        return scene;
    }
}
