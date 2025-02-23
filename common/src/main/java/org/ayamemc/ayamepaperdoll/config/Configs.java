/*
 *     Highly configurable PaperDoll mod. Forked from Extra Player Renderer.
 *     Copyright (C) 2024-2025  LucunJi(Original author), HappyRespawnanchor
 *
 *     This file is part of Ayame PaperDoll.
 *
 *     Ayame PaperDoll is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Ayame PaperDoll is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Ayame PaperDoll.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ayamemc.ayamepaperdoll.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.ayamemc.ayamepaperdoll.AyamePaperDoll;

import java.nio.file.Path;

import static org.ayamemc.ayamepaperdoll.AyamePaperDoll.CONFIGS;
import static org.ayamemc.ayamepaperdoll.AyamePaperDoll.MOD_ID;

public class Configs {
    private final static Minecraft MINECRAFT = Minecraft.getInstance();
    public static ConfigClassHandler<Configs> INSTANCE = ConfigClassHandler.createBuilder(Configs.class)
            .id(AyamePaperDoll.path("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Path.of("config/" + MOD_ID + "_v0.json"))
                    .build()
            )
            .build();

    public static Screen makeScreen(Screen lastScreen) {
        return getInstance().genScreen(lastScreen).generateScreen(lastScreen);
    }

    public YetAnotherConfigLib genScreen(Screen lastScreen) {
        final boolean isInLevel = MINECRAFT.level != null;
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("key.ayame_paperdoll.category"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.ayame_paperdoll.category.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("config.ayame_paperdoll.option.display_paperdoll"))
                                .description(OptionDescription.of(Component.translatable("config.ayame_paperdoll.option.display_paperdoll.desc")))
                                .binding(true, () -> this.displayPaperDoll, (newVal) -> this.displayPaperDoll = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build()
                        )

                        .option(LabelOption.create(Component.literal("预设")))

                        .name(Component.translatable("config.ayame_paperdoll.option.presets"))
                        .option(
                                ButtonOption.createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.presets.top_left"))
                                        .text(Component.empty())
                                        .action((yaclScreen, thisOption) -> {
                                            CONFIGS.offsetX = 0.08;
                                            CONFIGS.offsetY = 0.23;
                                            CONFIGS.rotationX = -4.96;
                                            CONFIGS.rotationY = -4.96;
                                            CONFIGS.rotationZ = 0D;
                                            CONFIGS.size = 0.1;
                                            CONFIGS.mirrored = true;
                                        }).build()
                        )
                        .option(
                                ButtonOption.createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.presets.top_right"))
                                        .text(Component.empty())
                                        .action((yaclScreen, thisOption) -> {
                                            CONFIGS.offsetX = 0.91;
                                            CONFIGS.offsetY = 0.23;
                                            CONFIGS.rotationX = -4.96;
                                            CONFIGS.rotationY = -4.96;
                                            CONFIGS.rotationZ = 0D;
                                            CONFIGS.size = 0.1;
                                            CONFIGS.mirrored = false;
                                        }).build()
                        )
                        .option(
                                ButtonOption.createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.presets.bottom_left"))
                                        .text(Component.empty())
                                        .action((yaclScreen, thisOption) -> {
                                            CONFIGS.offsetX = 0.14;
                                            CONFIGS.offsetY = 1.27;
                                            CONFIGS.rotationX = 0D;
                                            CONFIGS.rotationY = 0D;
                                            CONFIGS.rotationZ = 0D;
                                            CONFIGS.size = 0.29;
                                            CONFIGS.mirrored = true;
                                        }).build()
                        )
                        .option(
                                ButtonOption.createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.presets.bottom_right"))
                                        .text(Component.empty())
                                        .action((yaclScreen, thisOption) -> {
                                            CONFIGS.offsetX = 0.85;
                                            CONFIGS.offsetY = 1.27;
                                            CONFIGS.rotationX = 0D;
                                            CONFIGS.rotationY = 0D;
                                            CONFIGS.rotationZ = 0D;
                                            CONFIGS.size = 0.29;
                                            CONFIGS.mirrored = false;
                                        }).build()
                        )

                        .option(
                                ButtonOption.createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.button.visual_config_editor"))
                                        .action((yaclScreen, thisOption) -> {
                                            MINECRAFT.setScreen(new VisualConfigEditorScreen(lastScreen));
                                        })
                                        .available(isInLevel)
                                        .description(OptionDescription.of(Component.translatable("config.ayame_paperdoll.option.visual_config_editor.desc")))
                                        .build()
                        )
                        .option(
                                Option.<RotationMode>createBuilder()
                                        .name(Component.translatable("config.ayame_paperdoll.option.rotation_mode"))
                                        .description(OptionDescription.of(Component.translatable("config.ayame_paperdoll.option.rotation_mode.desc")))
                                        .binding(RotationMode.LOCK, () -> this.rotationMode, (newVal) -> this.rotationMode = newVal)
                                        .controller((rotationModeOption -> EnumControllerBuilder.create(rotationModeOption).enumClass(RotationMode.class)))
                                        .build()
                        ).build()

                )
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.ayame_paperdoll.category.rotations")
                        ).build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.ayame_paperdoll.category.postures")
                        ).build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("config.ayame_paperdoll.category.details")
                        ).build()
                ).build();

    }

    public static boolean isConfigScreen(Screen screen) {
        return screen != null && screen.getTitle().equals(Component.translatable("yacl3.config." + MOD_ID + ":config.title"));
    }

    public static boolean load() {
        return INSTANCE.load();
    }

    public static void save() {
        INSTANCE.save();
    }

    public static Configs getInstance() {
        return INSTANCE.instance();
    }

    public static final String GENERAL_CATEGORY = "general";
    public static final String ROTATIONS_CATEGORY = "rotations";
    public static final String POSTURES_CATEGORY = "postures";
    public static final String DETAILS_CATEGORY = "details";
    public static final String HIDDEN_CATEGORY = "hidden";

    //    public final SimpleOption<Boolean> displayPaperDoll = new SimpleOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("display_paperdoll"), true);
    @SerialEntry()
//    @AutoGen(category = GENERAL_CATEGORY)
//    @Boolean
    public boolean displayPaperDoll = true;

//    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @Label
//    public Component presetsLabel = Component.translatable("yacl3.config.ayame_paperdoll:label.presets");

    //    public final SimpleOption<RotationMode> rotationMode = new SimpleOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("rotation_mode"), RotationMode.LOCK);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @EnumCycler
    public RotationMode rotationMode = RotationMode.LOCK;
    //    public final SimpleNumericOption<Double> offsetX = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("offset_x"), 0.08, -0.5, 1.5);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = -0.5, max = 1.5, step = 0.01)
    public double offsetX = 0.08;
    //    public final SimpleNumericOption<Double> offsetY = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("offset_y"), 0.23, -0.5, 2.5);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = -0.5, max = 2.5, step = 0.01)
    public double offsetY = 0.23;
    //    public final SimpleNumericOption<Double> rotationX = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("rotation_x"), -4.96, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double rotationX = -4.96;
    //    public final SimpleNumericOption<Double> rotationY = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("rotation_y"), -4.96, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double rotationY = -4.96;
    //    public final SimpleNumericOption<Double> rotationZ = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("rotation_z"), 0D, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double rotationZ = 0D;
    //    public final SimpleNumericOption<Double> size = new SimpleNumericOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("size"), 0.1, 0D, 2D);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @DoubleSlider(min = 0D, max = 2D, step = 0.01)
    public double size = 0.1;
    //    public final SimpleOption<Boolean> mirrored = new SimpleOption<>(GENERAL_CATEGORY, AyamePaperDoll.path("mirrored"), true);
    @SerialEntry
//    @AutoGen(category = GENERAL_CATEGORY)
//    @Boolean
    public boolean mirrored = true;
    //    public final SimpleNumericOption<Double> pitch = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("pitch"), 0D, -90D, 90D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = -90D, max = 90D, step = 0.01)
    public double pitch = 0D;
    //    public final SimpleNumericOption<Double> pitchRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("pitch_range"), 20D, 0D, 90D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = 0D, max = 90D, step = 0.01)
    public double pitchRange = 20D;
    //    public final SimpleNumericOption<Double> headYaw = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("head_yaw"), -7.5D, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double headYaw = -7.5D;
    //    public final SimpleNumericOption<Double> headYawRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("head_yaw_range"), 0D, 0D, 180D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = 0D, max = 180D, step = 0.01)
    public double headYawRange = 0D;

    //    public final SimpleNumericOption<Double> bodyYaw = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("body_yaw"), 0D, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double bodyYaw = 0D;
    //    public final SimpleNumericOption<Double> bodyYawRange = new SimpleNumericOption<>(ROTATIONS_CATEGORY, AyamePaperDoll.path("body_yaw_range"), 0D, 0D, 180D);
    @SerialEntry
//    @AutoGen(category = ROTATIONS_CATEGORY)
//    @DoubleSlider(min = 0D, max = 180D, step = 0.01)
    public double bodyYawRange = 0D;
    //    public final SimpleOption<PoseOffsetMethod> poseOffsetMethod = new SimpleOption<>(POSTURES_CATEGORY, AyamePaperDoll.path("pose_offset_method"), PoseOffsetMethod.AUTO);
    @SerialEntry
//    @AutoGen(category = POSTURES_CATEGORY)
//    @EnumCycler
    public PoseOffsetMethod poseOffsetMethod = PoseOffsetMethod.AUTO;
    //    public final SimpleNumericOption<Double> sneakOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, AyamePaperDoll.path("sneak_offset_y"), -0.35, -3D, 3D);
    @SerialEntry
//    @AutoGen(category = POSTURES_CATEGORY)
//    @DoubleSlider(min = -3D, max = 3D, step = 0.01)
    public double sneakOffsetY = -0.35;
    //    public final SimpleNumericOption<Double> swimCrawlOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, AyamePaperDoll.path("swim_crawl_offset_y"), -1.22, -3D, 3D);
    @SerialEntry
//    @AutoGen(category = POSTURES_CATEGORY)
//    @DoubleSlider(min = -3D, max = 3D, step = 0.01)
    public double swimCrawlOffsetY = -1.22;
    //    public final SimpleNumericOption<Double> elytraOffsetY = new SimpleNumericOption<>(POSTURES_CATEGORY, AyamePaperDoll.path("elytra_offset_y"), -1.22, -3D, 3D);
    @SerialEntry
//    @AutoGen(category = POSTURES_CATEGORY)
//    @DoubleSlider(min = -3D, max = 3D, step = 0.01)
    public double elytraOffsetY = -1.22;
    //    public final SimpleOption<Boolean> hurtFlash = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("hurt_flash"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean hurtFlash = true;
    //    public final SimpleOption<Boolean> swingHands = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("swing_hands"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean swingHands = true;
    //    public final SimpleNumericOption<Double> lightDegree = new SimpleNumericOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("light_degree"), 0D, -180D, 180D);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @DoubleSlider(min = -180D, max = 180D, step = 0.01)
    public double lightDegree = 0D;
    //    public final SimpleOption<Boolean> useWorldLight = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("use_world_light"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean useWorldLight = true;
    //    public final SimpleNumericOption<Integer> worldLightMin = new SimpleNumericOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("world_light_min"), 2, 0, 15);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @IntSlider(min = 0, max = 15, step = 1)
    public int worldLightMin = 2;
    //    public final SimpleOption<Boolean> renderVehicle = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("render_vehicle"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean renderVehicle = true;
    //    public final SimpleOption<Boolean> pauseGameOnConfigScreen = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("pause_game_on_config_screen"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean pauseGameOnConfigScreen = true;
    //    public final SimpleOption<Boolean> disableConfigScreenBlur = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("disable_config_screen_blur"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean disableConfigScreenBlur = true;
    //    public final SimpleOption<Boolean> visibleDuringActivity = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("visible_during_activity"), false);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean visibleDuringActivity = false;
    //    public final SimpleOption<Boolean> hideUnderDebug = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("hide_under_debug"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean hideUnderDebug = true;
    //    public final SimpleOption<Boolean> hideOnScreenOpen = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("hide_on_screen_open"), false);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean hideOnScreenOpen = false;
    //    public final SimpleOption<Boolean> spectatorAutoSwitch = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("spectator_auto_switch"), true);
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @Boolean
    public boolean spectatorAutoSwitch = true;
    //    public final SimpleOption<String> playerName = new SimpleOption<>(DETAILS_CATEGORY, AyamePaperDoll.path("player_name"), "");
    @SerialEntry
//    @AutoGen(category = DETAILS_CATEGORY)
//    @StringField
    public String playerName = "";

    public interface NamedEnum extends NameableEnum {
        String name();

        @Override
        default Component getDisplayName() {
            return Component.translatable("config.ayame_paperdoll.enum.RotationMode." + name());
        }
    }

    public enum PoseOffsetMethod {
        AUTO, MANUAL, FORCE_STANDING, DISABLED
    }

    public enum RotationMode implements NamedEnum {
        UNLOCK, LOCK
    }

//    @FunctionalInterface
//    public interface Presets {
//        void load();
//
//        class PresetsBuilder {
//            private final List<Runnable> presets = new ArrayList<>();
//
//            public <T> PresetsBuilder with(ConfigOption<T> option, T value) {
//                this.presets.add(() -> option.setValue(value));
//                return this;
//            }
//
//            public Presets build() {
//                return () -> presets.forEach(Runnable::run);
//            }
//        }
//    }
}
