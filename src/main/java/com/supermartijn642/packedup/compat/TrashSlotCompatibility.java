package com.supermartijn642.packedup.compat;

import com.google.common.collect.ImmutableMap;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.screen.BackpackContainerScreen;
import net.blay09.mods.trashslot.api.event.RegisterTrashSlotContainerLayoutsEvent;
import net.blay09.mods.trashslot.api.layout.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Created 07/08/2026 by SuperMartijn642
 */
public class TrashSlotCompatibility {

    private static final Identifier PLAYER = Identifier.fromNamespaceAndPath("packedup", "player");
    private static final Identifier BACKPACK = Identifier.fromNamespaceAndPath("packedup", "backpack");

    private static void createSnaps(String identifier, Identifier rect,
                                    boolean top,
                                    boolean bottom,
                                    boolean right, boolean rightTop, boolean rightBottom,
                                    boolean left, boolean leftTop, boolean leftBottom,
                                    BiConsumer<Identifier,Snap> consumer){
        if(top){
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_top"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Range(new SnapCoordinateProvider.Left(rect, 12), new SnapCoordinateProvider.Right(rect, -28))),
                    Optional.of(new SnapCoordinateProvider.Top(rect, -15)),
                    SlotVisual.ATTACH_TOP
                )
            );
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_top_right"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Right(rect, -24)),
                    Optional.of(new SnapCoordinateProvider.Top(rect, -15)),
                    SlotVisual.ATTACH_TOP_RIGHT
                )
            );
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_top_left"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Left(rect, 8)),
                    Optional.of(new SnapCoordinateProvider.Top(rect, -15)),
                    SlotVisual.ATTACH_TOP_LEFT
                )
            );
        }
        if(bottom){
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_bottom"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Range(new SnapCoordinateProvider.Left(rect, 12), new SnapCoordinateProvider.Right(rect, -28))),
                    Optional.of(new SnapCoordinateProvider.Bottom(rect, -1)),
                    SlotVisual.ATTACH_BOTTOM
                )
            );
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_bottom_right"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Right(rect, -24)),
                    Optional.of(new SnapCoordinateProvider.Bottom(rect, -1)),
                    SlotVisual.ATTACH_BOTTOM_RIGHT
                )
            );
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_bottom_left"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Left(rect, 8)),
                    Optional.of(new SnapCoordinateProvider.Bottom(rect, -1)),
                    SlotVisual.ATTACH_BOTTOM_LEFT
                )
            );
        }
        if(left){
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_left"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Left(rect, -15)),
                    Optional.of(new SnapCoordinateProvider.Range(new SnapCoordinateProvider.Top(rect, 10), new SnapCoordinateProvider.Bottom(rect, -28))),
                    SlotVisual.ATTACH_LEFT
                )
            );
            if(leftTop){
                consumer.accept(
                    Identifier.fromNamespaceAndPath("packedup", identifier + "_left_top"),
                    new Snap(
                        Optional.of(new SnapCoordinateProvider.Left(rect, -15)),
                        Optional.of(new SnapCoordinateProvider.Top(rect, 7)),
                        SlotVisual.ATTACH_LEFT_TOP
                    )
                );
            }
            if(leftBottom){
                consumer.accept(
                    Identifier.fromNamespaceAndPath("packedup", identifier + "_left_bottom"),
                    new Snap(
                        Optional.of(new SnapCoordinateProvider.Left(rect, -15)),
                        Optional.of(new SnapCoordinateProvider.Bottom(rect, -24)),
                        SlotVisual.ATTACH_LEFT_BOTTOM
                    )
                );
            }
        }
        if(right){
            consumer.accept(
                Identifier.fromNamespaceAndPath("packedup", identifier + "_right"),
                new Snap(
                    Optional.of(new SnapCoordinateProvider.Right(rect, -1)),
                    Optional.of(new SnapCoordinateProvider.Range(new SnapCoordinateProvider.Top(rect, 10), new SnapCoordinateProvider.Bottom(rect, -28))),
                    SlotVisual.ATTACH_RIGHT
                )
            );
            if(rightTop){
                consumer.accept(
                    Identifier.fromNamespaceAndPath("packedup", identifier + "_right_top"),
                    new Snap(
                        Optional.of(new SnapCoordinateProvider.Right(rect, -1)),
                        Optional.of(new SnapCoordinateProvider.Top(rect, 7)),
                        SlotVisual.ATTACH_RIGHT_TOP
                    )
                );
            }
            if(rightBottom){
                consumer.accept(
                    Identifier.fromNamespaceAndPath("packedup", identifier + "_right_bottom"),
                    new Snap(
                        Optional.of(new SnapCoordinateProvider.Right(rect, -1)),
                        Optional.of(new SnapCoordinateProvider.Bottom(rect, -24)),
                        SlotVisual.ATTACH_RIGHT_BOTTOM
                    )
                );
            }
        }
    }

    private static final Map<Identifier,Snap> SNAPS_SEPARATE, SNAPS_COMBINED;

    static{
        ImmutableMap.Builder<Identifier,Snap> builder = ImmutableMap.builder();
        createSnaps("player", PLAYER, false, true, true, false, true, true, false, true, builder::put);
        createSnaps("backpack", BACKPACK, true, false, true, true, false, true, true, false, builder::put);
        SNAPS_SEPARATE = builder.build();
        builder = ImmutableMap.builder();
        createSnaps("combined", ScreenBoundsProvider.SCREEN_ID, true, true, true, true, true, true, true, true, builder::put);
        SNAPS_COMBINED = builder.build();
    }

    public static void register(){
        RegisterTrashSlotContainerLayoutsEvent.EVENT.register(e -> {
            e.registerLayout(PackedUp.container, new TrashContainerLayout() {
                @Override
                public List<Rect2i> getAllBounds(TrashSlotContainerContext context){
                    //noinspection unchecked
                    BackpackContainerScreen widget = ((WidgetContainerScreen<BackpackContainerScreen,?>)context.screen()).getWidget();
                    Rect2i playerBounds = widget.getPlayerInventoryBounds();
                    Rect2i backpackBounds = widget.getBackpackInventoryBounds();
                    return playerBounds.getX() == backpackBounds.getX() && playerBounds.getWidth() == backpackBounds.getWidth() ?
                        List.of(context.rect(ScreenBoundsProvider.SCREEN_ID).orElseThrow()) :
                        List.of(
                            context.rect(PLAYER).orElseThrow(),
                            context.rect(BACKPACK).orElseThrow()
                        );
                }

                @Override
                public Optional<Rect2i> getBounds(TrashSlotContainerContext context, Identifier identifier){
                    Rect2i bounds = ScreenBoundsProvider.SCREEN.get(context);
                    if(ScreenBoundsProvider.SCREEN_ID.equals(identifier))
                        return Optional.of(bounds);
                    //noinspection unchecked
                    BackpackContainerScreen widget = ((WidgetContainerScreen<BackpackContainerScreen,?>)context.screen()).getWidget();
                    if(identifier.equals(PLAYER)){
                        Rect2i playerBounds = widget.getPlayerInventoryBounds();
                        return Optional.of(new Rect2i(
                            bounds.getX() + playerBounds.getX(),
                            bounds.getY() + playerBounds.getY(),
                            playerBounds.getWidth(),
                            playerBounds.getHeight()
                        ));
                    }
                    if(identifier.equals(BACKPACK)){
                        Rect2i backpackBounds = widget.getBackpackInventoryBounds();
                        return Optional.of(new Rect2i(
                            bounds.getX() + backpackBounds.getX(),
                            bounds.getY() + backpackBounds.getY(),
                            backpackBounds.getWidth(),
                            backpackBounds.getHeight()
                        ));
                    }
                    return Optional.empty();
                }

                @Override
                public Optional<Snap> getSnap(TrashSlotContainerContext context, Identifier identifier){
                    return Optional.ofNullable(SNAPS_SEPARATE.get(identifier))
                        .or(() -> Optional.ofNullable(SNAPS_COMBINED.get(identifier)));
                }

                @Override
                public Map<Identifier,Snap> getSnaps(TrashSlotContainerContext context){
                    //noinspection unchecked
                    BackpackContainerScreen widget = ((WidgetContainerScreen<BackpackContainerScreen,?>)context.screen()).getWidget();
                    Rect2i playerBounds = widget.getPlayerInventoryBounds();
                    Rect2i backpackBounds = widget.getBackpackInventoryBounds();
                    return playerBounds.getX() == backpackBounds.getX() && playerBounds.getWidth() == backpackBounds.getWidth() ? SNAPS_COMBINED : SNAPS_SEPARATE;
                }

                @Override
                public Optional<Snap> getDefaultSnap(TrashSlotContainerContext context){
                    return this.getSnap(context, Identifier.fromNamespaceAndPath("packedup", "player_bottom_right"))
                        .or(() -> this.getSnap(context, Identifier.fromNamespaceAndPath("packedup", "combined_bottom_right")));
                }

                @Override
                public TrashSlotAvailability getAvailability(){
                    return TrashSlotAvailability.DEFAULT;
                }
            });
        });
    }
}
