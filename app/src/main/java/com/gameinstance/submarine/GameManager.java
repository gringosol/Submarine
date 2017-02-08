package com.gameinstance.submarine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.opengl.Matrix;

import com.gameinstance.submarine.audio.SoundManager;
import com.gameinstance.submarine.gameplay.Gameplay;
import com.gameinstance.submarine.gameplay.LevelLogic;
import com.gameinstance.submarine.gameplay.tasks.MobTask;
import com.gameinstance.submarine.ui.ComboBox;
import com.gameinstance.submarine.ui.LetterGenerator;
import com.gameinstance.submarine.ui.TextButton;
import com.gameinstance.submarine.ui.TextLine;
import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.TextureHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    static List<Primitive> primitives = new ArrayList<>();
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
    private static Gameplay gameplay;

    private static boolean drawBackMap = false;
    private static boolean startFromMenu = true;

    static int [] backTexHandle;
    static int [] radarTexHandle;

    static String locale = "en_US";

    static ComboBox languageComboBox;

    static SoundManager soundManager;

    static int tileCountX = 0;
    static int tileCountY = 0;
    static int tileCount = 0;
    static int tileFrameCount = 0;
    static int [] [] [] tileMap = null;

    public static void initGame(final GameRenderer renderer) {
        isMainMenu = startFromMenu;
        detectLocale();
        scene = new Scene(renderer);
        GameManager.renderer = renderer;


        String langOption = getOption(OPT_LANGUAGE);
        if (!"".equals(langOption)) {
            switch (langOption) {
                case "Русский":
                    setLocale("ru");
                    break;
                case "English":
                    setLocale("en");
                    break;
                case "Polski":
                    setLocale("pl");
                    break;
                default:
                    setLocale("en");
            }
        } else {
            setLocale(locale);
        }


        soundManager = new SoundManager();
        addLayers();
        TextureManager.init();
        LetterGenerator.init();
        texPrimitive = renderer.createPrimitiveTextured();
        addPrimitive(texPrimitive);
        Primitive colPrimitive = renderer.createPrimitiveColored();
        addPrimitive(colPrimitive);
        movablePrimitiveMap = new HashMap<>();
        movablePrimitiveMap.put(renderer.getProgramHandle("SimpleProgramHandle"), colPrimitive);
        movablePrimitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), texPrimitive);
        camera = new Camera();

        if (!startFromMenu) { //todo - test entity, exclude if branch in future
            LevelLoader.loadLevel(GameActivity.getActivity(), R.raw.testlevel, true);
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
        addPrimitive(viewPortPrimitive);
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
        gameplay = new Gameplay();
        gameplay.init();
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

    public static void readLandscapeJson(int jsMapId) {
        String jsMapStr = RawResourceReader.readTextFileFromRawResource(renderer.getActivityContext(), jsMapId);
        try {
            JSONObject jsMap = new JSONObject(jsMapStr);
            tileCountX = jsMap.getInt("width");
            tileCountY = jsMap.getInt("height");
            tileCount = jsMap.getInt("tilecount");
            JSONArray tileset = jsMap.getJSONArray("map");
            tileFrameCount = tileset.getJSONArray(0).length();
            tileMap = new int[tileCountY][tileCountX][tileFrameCount];
            for (int y = 0; y < tileCountX; y++) {
                for (int x = 0; x < tileCountY; x++) {
                    for (int k = 0; k < tileFrameCount; k++) {
                        tileMap[y][x][k] = tileset.getJSONArray(y * tileCountX + x).getInt(k);
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sprite [] createLandScapeTiled(int textureId, int pixelsPerUnit, float unitSize, Primitive primitive) {
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int [] texHandles = TextureHelper.loadTileset(renderer.getActivityContext(), textureId, pixelsPerUnit, tileCount);
        Sprite [] sprites = new Sprite[tileCountX * tileCountY];
        float left = -(tileCountX * unitSize) / 2.0f + 0.5f * unitSize;
        float top = (tileCountY * unitSize) / 2.0f - 0.5f * unitSize;
        for (int y = 0; y < tileCountY; y++) {
            for (int x = 0; x < tileCountX; x++) {
                int index = tileMap[y][x][0];
                sprites[y * tileCountX + x] = new Sprite(texHandles[index], primitiveMap,
                        unitSize, new float[] {left + x * unitSize, top - y * unitSize});
                if (tileFrameCount > 1) {
                    List<Integer> indices = new ArrayList<>();
                    indices.add(index);
                    for (int i = 1; i < tileFrameCount; i++) {
                        int idx = tileMap[y][x][i];
                        if (!indices.contains(idx)) {
                            indices.add(idx);
                        }
                    }
                    if (indices.size() > 1) {
                        Integer [] thandles = new Integer[indices.size()];
                        int k = 0;
                        for (int id : indices) {
                            thandles[k] = texHandles[id];
                            k++;
                        }
                        Animation animation = new Animation(500, thandles, true);
                        sprites[y * tileCountX + x].setAnimation(animation);
                    }
                }
            }
        }
        minX =  -(tileCountX * unitSize) / 2.0f;
        maxX =  (tileCountX * unitSize) / 2.0f;
        minY =  -(tileCountY * unitSize) / 2.0f;
        maxY =  (tileCountY * unitSize) / 2.0f;
        return sprites;
    }

    public static Sprite createRadarMap(int textureId, int pixelSize, Primitive primitive, int bigTileSize) {
        Map<Integer, Primitive> primitiveMap = new HashMap<>();
        primitiveMap.put(renderer.getProgramHandle("DefaultProgramHandle"), primitive);
        int texHandle = TextureHelper.loadRadarTexture(renderer.getActivityContext(), textureId, pixelSize, tileCountX, tileCountY, tileMap, bigTileSize);
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
        final TextButton resumeButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.resume, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuPause(false);
                    }
                }, -1);
        resumeButton.addToLayer(menu_pause);
        TextButton svButton = new TextButton(0.0f, 0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.save, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        saveGame(DEFAULT_SAVE);
                        showMenuPause(false);
                    }
                }, -1);
        svButton.addToLayer(menu_pause);
        TextButton ldButton = new TextButton(0.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.load, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuPause(false);
                        loadGame(DEFAULT_SAVE);
                    }
                }, -1);
        ldButton.addToLayer(menu_pause);
        TextButton optionsButton = new TextButton(0.0f, -0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.options, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuOptions(true);
                    }
                }, -1);
        optionsButton.addToLayer(menu_pause);
        TextButton exitButton = new TextButton(0.0f, -0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.exit, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showConfirmDialog(true);
                    }
                }, -1);
        exitButton.addToLayer(menu_pause);

        Layer menu_options = scene.getLayer("menu_options");
        TextButton resumeFromOptionsButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.back, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        if (!getString(languageComboBox.getValue()).equals(gameplay.getLanguageOption())) {
                            setOption(OPT_LANGUAGE, getString(languageComboBox.getValue()));
                            gameplay.setLanguageOption(getString(languageComboBox.getValue()));
                            renderer.getSurfaceView().queueEvent(new Runnable() {
                                @Override
                                public void run() {

                                        switch (languageComboBox.getValue()) {
                                            case R.string.russian:
                                                setLocale("ru");
                                                break;
                                            case R.string.english:
                                                setLocale("en");
                                                break;
                                            case R.string.polish:
                                                setLocale("pl");
                                                break;
                                            default:
                                                setLocale("en");
                                        }

                                    scene.reinitText();
                                }
                            });

                        }
                        showMenuOptions(false);
                    }
                }, -2);
        resumeFromOptionsButton.addToLayer(menu_options);

        languageComboBox = new ComboBox(0, 0.4f, 1.5f, 0.3f, new Integer[] {R.string.russian, R.string.english, R.string.polish}, 0.2f, -2) {
            @Override
            public void onValueChange(int newValue) {

            }
        };
        languageComboBox.addToLayer(menu_options);
        String langOption = getOption(OPT_LANGUAGE);
        if (!"".equals(langOption)) {
            switch (langOption) {
                case "Русский":
                    setLocale("ru");
                    languageComboBox.setValue(R.string.russian);
                    break;
                case "English":
                    setLocale("en");
                    languageComboBox.setValue(R.string.english);
                    break;
                case "Polski":
                    setLocale("pl");
                    languageComboBox.setValue(R.string.polish);
                    break;
                default:
                    setLocale("en");
                    languageComboBox.setValue(R.string.english);
            }
        } else {
            switch (locale) {
                case "ru_RU":
                    languageComboBox.setValue(R.string.russian);
                    break;
                case "en_US":
                    languageComboBox.setValue(R.string.english);
                    break;
                case "pl_PL":
                    languageComboBox.setValue(R.string.polish);
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

        TextLine langTextLine = new TextLine(R.string.language,
                new float[] {-1.8f, 0.4f}, 0.2f,
                GameManager.getRenderer());
        menu_options.addTextline(langTextLine);
        TextLine volumeTextLine = new TextLine(R.string.volume, new float[] {-1.8f, 0.0f}, 0.2f,
                GameManager.getRenderer());
        menu_options.addTextline(volumeTextLine);



        Layer menu_main = scene.getLayer("menu_main");
        TextButton newGameButton = new TextButton(0.0f, 0.8f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.new_game, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        startNewGame();
                    }
                }, -10);
        newGameButton.addToLayer(menu_main);
        TextButton ldButton1 = new TextButton(0.0f, 0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.load, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        loadGame(DEFAULT_SAVE);
                    }
                }, -10);
        ldButton1.addToLayer(menu_main);
        TextButton optionsButton1 = new TextButton(0.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.options, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        showMenuOptions(true);
                    }
                }, -10);
        optionsButton1.addToLayer(menu_main);
        TextButton exitButton1 = new TextButton(0.0f, -0.4f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.exit, 0.2f, movablePrimitiveMap,
                new Button.ClickListener() {
                    @Override
                    public void onClick() {
                        renderer.paused = true;
                        clearMemory();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }, -10);
        exitButton1.addToLayer(menu_main);


        Layer menu_confirm_dialog = scene.getLayer("menu_confirm_dialog");
        menu_confirm_dialog.addSprite(mainMenuBackSprite);
        TextButton yesButton = new TextButton(-1.0f, 0.0f, 1.5f, 0.3f, new int[] {
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.yes, 0.2f, movablePrimitiveMap,
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
                R.drawable.tbbackground, R.drawable.tbbackground1}, R.string.no, 0.2f, movablePrimitiveMap,
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

    public static void clearLevel() {
        scene.getLayer("landscape_back").clear();
        scene.getLayer("mobs_back").clear();
        scene.getLayer("landscape").clear();
        scene.getLayer("submarines").clear();
        scene.getLayer("ships_and_tanks").clear();
        scene.getLayer("aircrafts").clear();
        clearMovables();
        if (gameplay.getCurrentLevel() != null) {
            gameplay.getCurrentLevel().onClose();
        }
        levelId = 0;
    }

    public static void clearMovables() {
        List<Movable> movablesToClear = new ArrayList<>();
        for (Movable movable : scene.getMovables()) {
            if (movable instanceof Ship || movable instanceof Tank || movable instanceof Helicopter)
                movablesToClear.add(movable);
            if (movable instanceof Ship || movable instanceof Tank) {
                scene.getLayer("ships_and_tanks").removeSprite(movable.getSprite());
            }
            if (movable instanceof Helicopter) {
                scene.getLayer("aircrafts").removeSprite(movable.getSprite());
            }
            if (movable.getViewCircle() != null) {
                scene.getLayer("submarines").removeSprite(movable.getViewCircle());
            }
            if (movable.getSoundSource() != null) {
                movable.getSoundSource().stop();
            }
        }
        for (Movable movable : movablesToClear) {
            scene.getMovables().remove(movable);
        }
    }

    public static void nextLevel() {
        renderer.setPaused(true);
        renderer.getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {

                clearLevel();
                currentLevel++;
                if (currentLevel >= levelList.size())
                    currentLevel = 0;
                LevelLoader.loadLevel(GameActivity.getActivity(), levelList.get(currentLevel), true);

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
            List<JSONObject> ships = new ArrayList<>();
            List<JSONObject> tanks = new ArrayList<>();
            List<JSONObject> helis = new ArrayList<>();
            if (gameplay.getCurrentLevel() != null) {
                LevelLogic level = gameplay.getCurrentLevel();
                savedData.put("levelclass", level.getClass().getName());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(level);
                    JSONArray byteArray = new JSONArray();
                    for (byte b : outputStream.toByteArray()) {
                        byteArray.put(b);
                    }
                    savedData.put("levelstate", byteArray);
                    objectOutputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            for (Movable movable : scene.getMovables()) {
                JSONObject jsMovable = new JSONObject();
                jsMovable.put("x", movable.getSprite().getPosition()[0]);
                jsMovable.put("y", movable.getSprite().getPosition()[1]);
                jsMovable.put("angle", movable.getAngle());
                if (movable.getType() != null)
                    jsMovable.put("type", movable.getType());
                if (movable instanceof Ship) {
                    ships.add(jsMovable);
                }
                if (movable instanceof Tank) {
                    tanks.add(jsMovable);
                }
                if (movable instanceof Helicopter) {
                    helis.add(jsMovable);
                }
                if (movable.getCurrentTask() != null) {
                    MobTask task  = movable.getCurrentTask();
                    jsMovable.put("taskclass", task.getClass().getName());
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(task);
                        JSONArray byteArray = new JSONArray();
                        for (byte b : outputStream.toByteArray()) {
                            byteArray.put(b);
                        }
                        jsMovable.put("taskstate", byteArray);
                        objectOutputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
            if (ships.size() > 0) {
                JSONArray shipArray = new JSONArray(ships);
                savedData.put("ships", shipArray);
            }
            if (ships.size() > 0) {
                JSONArray tankArray = new JSONArray(tanks);
                savedData.put("tanks", tankArray);
            }
            if (ships.size() > 0) {
                JSONArray helisArray = new JSONArray(helis);
                savedData.put("helis", helisArray);
            }
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
        String s;
        SharedPreferences sharedPreferences = GameActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
        s = sharedPreferences.getString(filename, "");
        if (!s.equals("")) {
            try {
                final JSONObject loadedData = new JSONObject(s);
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
                            LevelLoader.loadLevel(GameActivity.getActivity(), levId, false);
                        } else {
                            clearMovables();
                        }
                        int j = 0;
                        for (Integer lev : levelList) {
                            if (lev.equals(levId)) {
                                currentLevel = j;
                                break;
                            }
                            j++;
                        }
                        submarineMovable.getSprite().setPosition(x, y);
                        submarineMovable.setAngle(angle);
                        submarineMovable.setTarget(new float[]{x, y});
                        try {
                            if (loadedData.has("ships")) {
                                JSONArray jsShips = loadedData.getJSONArray("ships");
                                for (int i = 0; i < jsShips.length(); i++) {
                                    JSONObject jsShip = jsShips.getJSONObject(i);
                                    LevelLoader.loadShip(jsShip);
                                }
                            }
                            if (loadedData.has("tanks")) {
                                JSONArray jsTAnks = loadedData.getJSONArray("tanks");
                                for (int i = 0; i < jsTAnks.length(); i++) {
                                    JSONObject jsTank = jsTAnks.getJSONObject(i);
                                    LevelLoader.loadTank(jsTank);
                                }
                            }
                            if (loadedData.has("helis")) {
                                JSONArray jsHelis = loadedData.getJSONArray("helis");
                                for (int i = 0; i < jsHelis.length(); i++) {
                                    JSONObject jsHeli = jsHelis.getJSONObject(i);
                                    LevelLoader.loadHelicopter(jsHeli);
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException("Ошибка чтения json");
                        }
                        if (isMainMenu) {
                            isMainMenu = false;
                            showMainMenu(false);
                        }
                        gameplay.setCurrentLevel(LevelLoader.loadLevelState(loadedData));
                        gameplay.reinitGame();
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
        gameplay = new Gameplay();
        gameplay.init();
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
                    LevelLoader.loadLevel(GameActivity.getActivity(), levelList.get(0), true);
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

    public static Gameplay getGameplay() {
        return gameplay;
    }

    public static Sprite addSprite(int texId, float x, float y, float w, float h) {
        int texHandle = TextureManager.getTextureHandle(getRenderer().getActivityContext(), texId);
        return new Sprite(texHandle, getMovablePrimitiveMap(), new float[] {w, h}, new float[] {x, y});
    }

    public static void showMessage(int messageId, float x, float y, long duration) {
        final TextLine textLine = new TextLine(messageId, new float[] {x, y}, 0.2f, getRenderer());
        scene.getLayer("hud").addTextline(textLine);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scene.getLayer("hud").removeTextLine(textLine);
            }
        }, duration);
    }

    public static void clearMemory() {
        for (Primitive primitive : primitives) {
            if (primitive != null)
                primitive.onDestroy();
        }
        primitives.clear();
        soundManager.destroy();
    }

    public static void addPrimitive(Primitive primitive) {
        primitives.add(primitive);
    }

    public static SoundManager getSoundManager() {
        return soundManager;
    }

    public static String getString(int resId) {
        return GameActivity.getActivity().getResources().getString(resId);
    }

    public static void setLocale(String l) {
        Locale locale = new Locale(l);
        Locale.setDefault(locale);
        Configuration config = GameActivity.getActivity().getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        GameActivity.getActivity().getBaseContext().getResources().updateConfiguration(config,
                GameActivity.getActivity().getBaseContext().getResources().getDisplayMetrics());
    }
}
