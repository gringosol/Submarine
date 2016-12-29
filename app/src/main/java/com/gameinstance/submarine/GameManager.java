package com.gameinstance.submarine;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Matrix;

import com.gameinstance.submarine.ui.ComboBox;
import com.gameinstance.submarine.ui.TextButton;
import com.gameinstance.submarine.ui.TextLine;
import com.gameinstance.submarine.utils.TextureHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
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
    private static float minX = -1.0f;
    private static float maxX = 1.0f;
    private static float minY = -1.0f;
    private static float maxY = 1.0f;
    static Primitive texPrimitive;
    static Map<Integer, Primitive> movablePrimitiveMap;
    static Submarine submarineMovable;
    static List<Integer> levelList = Arrays.asList(R.raw.testlevel, R.raw.testlevel2);
    static int currentLevel = 0;
    static Camera camera;
    static int levelId = 0;
    private static final String DEFAULT_SAVE = "quicksave";
    private static final String OPT_LANGUAGE = "option_language";
    private static final String OPT_VOLUME = "option_volume";
    private static final float radarScale = 0.25f;
    private static final int radarViewportSize = 512;
    private static boolean isMainMenu;

    private static boolean drawBackMap = false;
    private static boolean startFromMenu = true;

    static int [] backTexHandle;
    static int [] radarTexHandle;

    static String locale = "en_US";

    public static void initGame(final GameRenderer renderer) {
        isMainMenu = startFromMenu;
        detectLocale();
        scene = new Scene(renderer);
        GameManager.renderer = renderer;
        addLayers();
        texPrimitive = renderer.createPrimitiveTextured();
        Primitive colPrimitive = renderer.createPrimitiveColored();
        movablePrimitiveMap = new HashMap<>();
        movablePrimitiveMap.put(renderer.getProgramHandle("SimpleProgramHandle"), colPrimitive);
        movablePrimitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), texPrimitive);
        camera = new Camera();

        if (!startFromMenu) { //todo - test entity, exclude if branch in future
            LevelLoader.loadLevel(GameActivity.getActivity(), R.raw.testlevel);
        } else {
            InputController.setMinOrder(-10);
            InputController.setMaxOrder(-10);
        }

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

        addGui();


        final float[] texCoordinateData =  {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f};
        Primitive viewPortPrimitive = renderer.createPrimitiveTextured(texCoordinateData);
        Map<Integer, Primitive> primitiveMap =
                Collections.singletonMap(renderer.getProgramHandle("DefaultProgramHandle"), viewPortPrimitive);
        if (drawBackMap) {
            Sprite backMap = new Sprite(backTexHandle[0], primitiveMap, 1.0f, new float[]{-1.0f, 0.5f});
            scene.getLayer("hud").addSprite(backMap);
        }
        Sprite radarViewPort = new Sprite(radarTexHandle[0], primitiveMap, 1.0f, new float[]{1.4f, 0.5f});
        scene.getLayer("hud").addSprite(radarViewPort);
        Sprite radarHudSprite = new Sprite(renderer, R.drawable.radarhud, primitiveMap,
                2.0f / radarScale, 2.0f / radarScale);
        scene.getLayer("radarhud").addSprite(radarHudSprite);
        Sprite radarArrowSprite = new Sprite(renderer, R.drawable.radararrow, primitiveMap,
                2.0f / radarScale, 2.0f / radarScale);
        scene.getLayer("radarhud").addSprite(radarArrowSprite);
        final Movable radarArrowMovable = new Movable(radarArrowSprite) {
            @Override
            public void update() {
                setAngle(getAngle() - 0.5f);
            }
        };
        scene.addMovable(radarArrowMovable);
        radarArrowMovable.setCollide(false);
    }

    public static void setCamera() {
        camera.setTarget(submarineMovable.getSprite());
        camera.setBounds(minX, maxX, minY, maxY);
        renderer.setCamera(camera);
    }

    public static Sprite [] createLandScape(int textureId, int pixelsPerUnit, float unitSize, Primitive primitive) {
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
                sprites[j * n + i] = new Sprite(texHandles[j * n + i + 2], primitiveMap,
                        unitSize, new float[] {left + i * unitSize, top - j * unitSize});
            }
        }
        minX =  -(n * unitSize) / 2.0f;
        maxX =  (n * unitSize) / 2.0f;
        minY =  -(m * unitSize) / 2.0f;
        maxY =  (m * unitSize) / 2.0f;
        return sprites;
    }

    public static Sprite createRadarMap(int textureId, int pixelSize, Primitive primitive) {
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int texHandle = TextureHelper.loadRadarTexture(renderer.getActivityContext(), textureId, pixelSize);
        return new Sprite(texHandle, primitiveMap, new float[] {maxX - minX, maxY - minY},
                new float[] {0, 0});
    }

    private static void addLayers() {
        backTexHandle = renderer.createViewportTarget(256);
        radarTexHandle = renderer.createViewportTarget(radarViewportSize);
        scene.addLayerSet("BackBuffer", new Layerset(Arrays.asList("landscape_back", "mobs_back"),
                backTexHandle, renderer.getDefaultProjectionMatrix(), new int[] {256, 256}, true));
        float [] radarProjMatrix = new float[16];
        Matrix.setIdentityM(radarProjMatrix, 0);
        Matrix.scaleM(radarProjMatrix, 0, radarScale, radarScale, 1.0f);
        scene.addLayerSet("Radar", new Layerset(Arrays.asList("radarmap", "radarhud", "submarines", "ships_and_tanks",
                "aircrafts"), radarTexHandle, radarProjMatrix, new int[] {radarViewportSize, radarViewportSize}, false));
        scene.addLayerSet("Menu", new Layerset(Arrays.asList("menu_main", "menu_pause", "menu_options",
                "menu_confirm_dialog"), null, renderer.getDefaultProjectionMatrix(), null, false));
        scene.addLayerSet("Front", new Layerset(Arrays.asList("landscape", "submarines", "ships_and_tanks",
                "aircrafts", "hud"), null, renderer.getDefaultProjectionMatrix(), null, false));
        scene.getLayerSets().get("Menu").setEnabled(startFromMenu);
        scene.getLayerSets().get("BackBuffer").setEnabled(!startFromMenu);
        scene.getLayerSets().get("Radar").setEnabled(!startFromMenu);
        scene.getLayerSets().get("Front").setEnabled(!startFromMenu);
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
        Layer radarmap = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("radarmap", radarmap);
        Layer radarhud = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        radarhud.isGui = true;
        scene.addLayer("radarhud", radarhud);
        Layer menu_main = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("menu_main", menu_main);
        menu_main.isGui = true;
        menu_main.visible = startFromMenu;
        Layer menu_pause = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("menu_pause", menu_pause);
        menu_pause.isGui = true;
        menu_pause.visible = false;
        Layer menu_options = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("menu_options", menu_options);
        menu_options.isGui = true;
        menu_options.visible = false;
        Layer menu_confirm_dialog = new Layer(renderer.getProgramHandle("DefaultProgramHandle"));
        scene.addLayer("menu_confirm_dialog", menu_confirm_dialog);
        menu_confirm_dialog.isGui = true;
        menu_confirm_dialog.visible = false;
    }

    private static void addGui() {
        Sprite mainMenuBackSprite = new Sprite(renderer, R.drawable.submarinebackground, movablePrimitiveMap,
                2.0f, 2.0f);
        scene.getLayer("menu_pause").addSprite(mainMenuBackSprite);
        Sprite optionsMenuBackSprite = new Sprite(renderer, R.drawable.optionsbackground, movablePrimitiveMap,
                4.0f, 2.0f);
        scene.getLayer("menu_options").addSprite(optionsMenuBackSprite);
        Button stopButton = new Button(renderer, new int [] {R.drawable.stop, R.drawable.stop1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.setMotionEnabled(false);
            }
        }, new float[] {1.5f, -0.87f});

        Button emergeButton = new Button(renderer, new int [] {R.drawable.emerge, R.drawable.emerge1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.emerge();
            }
        }, new float[] {0.95f, -0.5f});

        Button plungeButton = new Button(renderer, new int [] {R.drawable.plunge, R.drawable.plunge1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                submarineMovable.plunge();
            }
        }, new float[] {1.5f, -0.5f});
        Button nextLevelButton = new Button(renderer, new int [] {R.drawable.nextlevel, R.drawable.nextlevel1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                nextLevel();
            }
        }, new float[] {0.95f, -0.87f});
        Button saveButton = new Button(renderer, new int [] {R.drawable.save, R.drawable.save1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                saveGame(DEFAULT_SAVE);
            }
        }, new float[] {0.95f, -0.13f});
        Button loadButton = new Button(renderer, new int [] {R.drawable.load, R.drawable.load1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
                loadGame(DEFAULT_SAVE);
            }
        }, new float[] {1.5f, -0.13f});
        Button menuButton = new Button(renderer, new int [] {R.drawable.menubutton, R.drawable.menubutton1},
                movablePrimitiveMap, 0.25f, 0.25f, new Button.ClickListener() {
            @Override
            public void onClick() {
              showMenuPause(true);
            }
        }, new float[] {0.7f, -0.13f});
        Layer hud = scene.getLayer("hud");
        hud.addSprite(stopButton);
        hud.addSprite(emergeButton);
        hud.addSprite(plungeButton);
        hud.addSprite(nextLevelButton);
        hud.addSprite(saveButton);
        hud.addSprite(loadButton);
        hud.addSprite(menuButton);
        Layer menu_pause = scene.getLayer("menu_pause");
        TextButton resumeButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Продолжить", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuPause(false);
                    }
                }, -1);
        resumeButton.addToLayer(menu_pause);
        TextButton svButton = new TextButton(0.0f, 0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Сохранить", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        saveGame(DEFAULT_SAVE);
                        showMenuPause(false);
                    }
                }, -1);
        svButton.addToLayer(menu_pause);
        TextButton ldButton = new TextButton(0.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Загрузить", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuPause(false);
                        loadGame(DEFAULT_SAVE);
                    }
                }, -1);
        ldButton.addToLayer(menu_pause);
        TextButton optionsButton = new TextButton(0.0f, -0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Настройки", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuOptions(true);
                    }
                }, -1);
        optionsButton.addToLayer(menu_pause);
        TextButton exitButton = new TextButton(0.0f, -0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Выход", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showConfirmDialog(true);
                    }
                }, -1);
        exitButton.addToLayer(menu_pause);

        Layer menu_options = scene.getLayer("menu_options");
        TextButton resumeFromOptionsButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Назад", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuOptions(false);
                    }
                }, -2);
        resumeFromOptionsButton.addToLayer(menu_options);

        ComboBox languageComboBox = new ComboBox(0, 0.4f, 1.5f, 0.3f, new String[] {"Русский", "English", "Polski"}, 0.2f, -2) {
            @Override
            public void onValueChange(String newValue) {
                setOption(OPT_LANGUAGE, newValue);
            }
        };
        languageComboBox.addToLayer(menu_options);
        String langOption = getOption(OPT_LANGUAGE);
        if (!"".equals(langOption)) {
            languageComboBox.setValue(langOption);
        } else {
            switch (locale) {
                case "ru_RU":
                    languageComboBox.setValue("Русский");
                    break;
                case "en_US":
                    languageComboBox.setValue("English");
                    break;
                case "pl_PL":
                    languageComboBox.setValue("Polski");
                    break;
            }
        }
        Slider volumeSlider = new Slider(0, 0, 1.5f, 0.3f, -2) {
            @Override
            public void onValueChange(Float value) {
                setOption(OPT_VOLUME, value.toString());
            }
        };
        volumeSlider.addToLayer(menu_options);
        String volumeOption = getOption(OPT_VOLUME);
        if (!"".equals(volumeOption)) {
            volumeSlider.setValue(Float.parseFloat(volumeOption));
        }

        TextLine langTextLine = new TextLine("Язык", new float[] {-1.8f, 0.4f}, 0.2f,
                GameManager.getRenderer());
        menu_options.addTextline(langTextLine);
        TextLine volumeTextLine = new TextLine("Громкость", new float[] {-1.8f, 0.0f}, 0.2f,
                GameManager.getRenderer());
        menu_options.addTextline(volumeTextLine);



        Layer menu_main = scene.getLayer("menu_main");
        TextButton newGameButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Новая игра", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        startNewGame();
                    }
                }, -10);
        newGameButton.addToLayer(menu_main);
        TextButton ldButton1 = new TextButton(0.0f, 0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Загрузить", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        loadGame(DEFAULT_SAVE);
                        showMainMenu(false);//todo replace
                    }
                }, -10);
        ldButton1.addToLayer(menu_main);
        TextButton optionsButton1 = new TextButton(0.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Настройки", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuOptions(true);
                    }
                }, -10);
        optionsButton1.addToLayer(menu_main);
        TextButton exitButton1 = new TextButton(0.0f, -0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Выход", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }, -10);
        exitButton1.addToLayer(menu_main);


        Layer menu_confirm_dialog = scene.getLayer("menu_confirm_dialog");
        menu_confirm_dialog.addSprite(mainMenuBackSprite);
        TextButton yesButton = new TextButton(-1.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Да", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        renderer.getSurfaceView().queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                showConfirmDialog(false);
                                clearLevel();
                                showMainMenu(true);
                            }
                        });
                    }
                }, -11);
        yesButton.addToLayer(menu_confirm_dialog);
        TextButton noButton = new TextButton(1.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, "Нет", 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showConfirmDialog(false);
                    }
                }, -11);
        noButton.addToLayer(menu_confirm_dialog);

    }

    public static Scene getScene() {
        return scene;
    }

    public static Primitive getTexPrimitive() {
        return texPrimitive;
    }

    public static GameRenderer getRenderer() {
        return renderer;
    }

    public static Map<Integer, Primitive> getMovablePrimitiveMap() {
        return movablePrimitiveMap;
    }

    public static void setSubmarineMovable(Submarine submarine) {
        submarineMovable = submarine;
    }

    private static void clearLevel() {
        scene.getLayer("landscape_back").clear();
        scene.getLayer("mobs_back").clear();
        scene.getLayer("landscape").clear();
        scene.getLayer("submarines").clear();
        scene.getLayer("ships_and_tanks").clear();
        scene.getLayer("aircrafts").clear();
    }

    private static void nextLevel() {
        renderer.setPaused(true);
        renderer.getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {

                clearLevel();
                currentLevel++;
                if (currentLevel >= levelList.size())
                    currentLevel = 0;
                LevelLoader.loadLevel(GameActivity.getActivity(), levelList.get(currentLevel));

            }
        });
        renderer.setPaused(false);
    }

    public static void setLevelId(int i) {
        levelId = i;
    }


    public static void saveGame(String filename) {
        JSONObject savedData = new JSONObject();
        try {
            savedData.put("levelId", levelId);
            JSONObject submarine = new JSONObject();
            submarine.put("x", submarineMovable.getSprite().getPosition()[0]);
            submarine.put("y", submarineMovable.getSprite().getPosition()[1]);
            submarine.put("angle", submarineMovable.getAngle());
            savedData.put("submarine", submarine);
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка сохраненя json");
        }
        String s  = savedData.toString();
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(filename, s);
        editor.apply();
    }

    public static void loadGame(String filename) {
        isMainMenu = false;
        String s;
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        s = sharedPreferences.getString(filename, "");
        if (!s.equals("")) {
            try {
                JSONObject loadedData = new JSONObject(s);
                final int levId = loadedData.getInt("levelId");
                JSONObject submarine = loadedData.getJSONObject("submarine");
                final float x = (float)submarine.getDouble("x");
                final float y = (float)submarine.getDouble("y");
                final float angle = (float)submarine.getDouble("angle");

                renderer.getSurfaceView().queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (levId != levelId) {
                            clearLevel();
                            currentLevel++;
                            LevelLoader.loadLevel(GameActivity.getActivity(), levId);
                        }
                        submarineMovable.getSprite().setPosition(x, y);
                        submarineMovable.setAngle(angle);
                        submarineMovable.setTarget(new float[] {x, y});
                    }
                });

            } catch (JSONException e) {
                throw new RuntimeException("Ошибка чтения json");
            }
        }
    }

    public static Submarine getSubmarineMovable() {
        return submarineMovable;
    }

    public static void showMenuPause(boolean show) {
        scene.getLayerSets().get("Menu").setEnabled(show);
        scene.getLayerSets().get("BackBuffer").setEnabled(!show);
        scene.getLayerSets().get("Radar").setEnabled(!show);
        scene.getLayerSets().get("Front").setEnabled(!show);
        if (show) {
            InputController.setMaxOrder(-1);
            InputController.setMinOrder(-1);
            scene.getLayer("menu_pause").visible = true;
        } else {
            InputController.setMaxOrder(100);
            InputController.setMinOrder(0);
            scene.getLayer("menu_pause").visible = false;
        }
    }

    public static void showConfirmDialog(boolean show) {
        if (show) {
            InputController.setMaxOrder(-11);
            InputController.setMinOrder(-11);
            scene.getLayer("menu_confirm_dialog").visible = true;
        } else {
            InputController.setMaxOrder(-10);
            InputController.setMinOrder(-10);
            scene.getLayer("menu_confirm_dialog").visible = false;
        }
    }

    public static void showMainMenu(boolean show) {
        scene.getLayerSets().get("Menu").setEnabled(show);
        scene.getLayerSets().get("BackBuffer").setEnabled(!show);
        scene.getLayerSets().get("Radar").setEnabled(!show);
        scene.getLayerSets().get("Front").setEnabled(!show);
        if (show) {
            isMainMenu = true;
            InputController.setMaxOrder(-10);
            InputController.setMinOrder(-10);
            scene.getLayer("menu_main").visible = true;
            scene.getLayer("menu_pause").visible = false;
        } else {
            isMainMenu = false;
            InputController.setMaxOrder(100);
            InputController.setMinOrder(0);
            scene.getLayer("menu_main").visible = false;
        }
    }

    public static void startNewGame() {
        isMainMenu = false;
        renderer.getSurfaceView().queueEvent( new Runnable() {
                @Override
                public void run() {
                    renderer.setPaused(true);
                    scene.getLayerSets().get("Menu").setEnabled(false);
                    scene.getLayerSets().get("BackBuffer").setEnabled(true);
                    scene.getLayerSets().get("Radar").setEnabled(true);
                    scene.getLayerSets().get("Front").setEnabled(true);
                    InputController.setMaxOrder(100);
                    InputController.setMinOrder(0);
                    scene.getLayer("menu_main").visible = false;
                    LevelLoader.loadLevel(GameActivity.getActivity(), levelList.get(0));
                    renderer.setPaused(false);
                }
        } );
    }

    public static void showMenuOptions(boolean show) {
        if (show) {
            InputController.setMaxOrder(-2);
            InputController.setMinOrder(-2);
            scene.getLayer("menu_options").visible = true;
        } else {
            if (isMainMenu) {
                InputController.setMaxOrder(-10);
                InputController.setMinOrder(-10);
            } else {
                InputController.setMaxOrder(-1);
                InputController.setMinOrder(-1);
            }
            scene.getLayer("menu_options").visible = false;
        }
    }

    public static void setOption(String optionName, String optionValue) {
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(optionName, optionValue);
        editor.apply();
    }

    public static String getOption(String optionName) {
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(optionName, "");
    }

    public static void detectLocale() {
                locale = GameActivity.getActivity().getResources().getConfiguration().locale.toString();
    }
}
