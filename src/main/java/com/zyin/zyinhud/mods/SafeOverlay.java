package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.Localization;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Safe Overlay renders an overlay onto the game world showing which areas
 * mobs can spawn on.
 */
public class SafeOverlay extends ZyinHUDModBase {
    /**
     * Enables/Disables this Mod
     */
    public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     *
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled() {
        return Enabled = !Enabled;
    }
    
    public static EntityLiving zombie = null;
    
    /**
     * The current mode for this mod
     */
    public static Modes Mode;

    /**
     * The enum for the different types of Modes this mod can have
     */
    public static enum Modes {
        /**
         * Off modes.
         */
        OFF(Localization.get("safeoverlay.mode.off")),
        /**
         * On modes.
         */
        ON(Localization.get("safeoverlay.mode.on"));

        private String friendlyName;

        private Modes(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        /**
         * Sets the next availble mode for this mod
         *
         * @return the modes
         */
        public static Modes ToggleMode() {
            return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        }

        /**
         * Gets the mode based on its internal name as written in the enum declaration
         *
         * @param modeName the mode name
         * @return modes
         */
        public static Modes GetMode(String modeName) {
            try {
                return Modes.valueOf(modeName);
            } catch (IllegalArgumentException e) {
                return values()[0];
            }
        }

        /**
         * Get friendly name string.
         *
         * @return the string
         */
        public String GetFriendlyName() {
            return friendlyName;
        }
    }

    /**
     * USE THE Getter/Setter METHODS FOR THIS!!
     * <p>
     * Calculate locations in a cube with this radius around the player.
     * <br>
     * Actual area calculated: (drawDistance*2)^3
     * <p>
     * drawDistance = 2 = 64 blocks (min)
     * <br>
     * drawDistance = 20 = 64,000 blocks (default)
     * <br>
     * drawDistance = 80 = 4,096,000 blocks
     * <br>
     * drawDistance = 175 = 42,875,000 blocks (max)
     */
    protected int drawDistance = 20;
    /**
     * The constant defaultDrawDistance.
     */
    public static final int defaultDrawDistance = 20;
    /**
     * The constant minDrawDistance.
     */
    public static final int minDrawDistance = 2;    //can't go lower than 2. setting this to 1 dispays nothing
    /**
     * The constant maxDrawDistance.
     */
    public static final int maxDrawDistance = 175;    //175 is the edge of the visible map on far

    /**
     * The transprancy of the "X" marks when rendered, between (0.1 and 1]
     */
    private float unsafeOverlayTransparency;
    private float unsafeOverlayMinTransparency = 0.11f;
    private float unsafeOverlayMaxTransparency = 1f;

    private static boolean displayInNether = false;
    private boolean renderUnsafePositionsThroughWalls = false;

    private BlockPos playerPosition;

    private static List<BlockPos> unsafePositionCache = new ArrayList<>();    //used during threaded calculations
    private static List<BlockPos> unsafePositions = new ArrayList<>();        //used during renderinig

    private Thread safeCalculatorThread = null;

    /**
     * Use this instance of the Safe Overlay for method calls.
     */
    public static SafeOverlay instance = new SafeOverlay();


    /**
     * Instantiates a new Safe overlay.
     */
    protected SafeOverlay() {
        playerPosition = new BlockPos(0, 0, 0);

        //Don't let multiple threads access this list at the same time by making it a Synchronized List
        unsafePositionCache = Collections.synchronizedList(new ArrayList<BlockPos>());
    }

    /**
     * This thead will calculate unsafe positions around the player given a Y coordinate.
     * <p>
     * <b>Single threaded</b> performance (with drawDistance=80):
     * <br>Average CPU usage: 24%
     * <br>Time to calculate all unsafe areas: <b>305 ms</b>
     * <p>
     * <b>Multi threaded</b> performance (with drawDistance=80):
     * <br>Average CPU usage: 25-35%
     * <br>Time to calculate all unsafe areas: <b>100 ms</b>
     * <p>
     * Machine specs when this test took place: Core i7 2.3GHz, 8GB DDR3, GTX 260
     * <br>With vanilla textures, far render distance, superflat map.
     */
    class SafeCalculatorThread extends Thread {
        /**
         * The Cached player position.
         */
        BlockPos cachedPlayerPosition;

        /**
         * Instantiates a new Safe calculator thread.
         *
         * @param playerPosition the player position
         */
        SafeCalculatorThread(BlockPos playerPosition) {
            super("Safe Overlay Calculator Thread");
            this.cachedPlayerPosition = playerPosition;
            SafeOverlay.zombie = new EntityZombie(mc.player.world);
            //Start the thread
            start();
        }

        //This is the entry point for the thread after start() is called.
        public void run() {
            unsafePositionCache.clear();
            try {

                for (int x = -drawDistance; x < drawDistance; x++) {
                    for (int y = -drawDistance; y < drawDistance; y++) {
                        for (int z = -drawDistance; z < drawDistance; z++) {
                            BlockPos pos = new BlockPos(cachedPlayerPosition.getX() + x,
                                    cachedPlayerPosition.getY() + y,
                                    cachedPlayerPosition.getZ() + z);

                            if (CanMobsSpawnAtPosition(pos)) {
                                unsafePositionCache.add(pos);
                            }
                        }
                    }
                    sleep(8);
                }
            } catch (InterruptedException e) {
                //this can happen if the Safe Overlay is turned off or if Minecraft closes while the thread is sleeping
            }
        }
    }


    /**
     * Determines if any mob can spawn at a position. Works very well at detecting
     * if bipeds or spiders can spawn there.
     *
     * @param pos Position of the block whos surface gets checked
     * @return boolean
     */
    public static boolean CanMobsSpawnAtPosition(BlockPos pos) {
        //if a mob can spawn here, add it to the unsafe positions cache so it can be rendered as unsafe
        //4 things must be true for a mob to be able to spawn here:
        //1) mobs need to be able to spawn on top of this block (block with a solid top surface)
        //2) mobs need to be able to spawn inside of the block above (air, button, lever, etc)
        //3) needs < 8 light level
        if (mc.player == null || mc.player.world == null) {
            return false;
        }
        World world = mc.player.world;
        boolean canSpawn = false;
        canSpawn = WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, pos);
        if (canSpawn) {
            zombie.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            canSpawn = zombie.isNotColliding();
            if (!SafeOverlay.displayInNether && world.getBlockState(pos).getBlock() instanceof BlockMagma) {
                canSpawn = false;
            }
        }
        return canSpawn && mc.world.getLightFor(EnumSkyBlock.BLOCK, pos) < 8;
    }


    /**
     * Renders all unsafe areas around the player.
     * It will only recalculate the unsafe areas once every [updateFrequency] milliseconds
     *
     * @param partialTickTime the partial tick time
     */
    public void RenderAllUnsafePositionsMultithreaded(float partialTickTime) {
        if (!SafeOverlay.Enabled || Mode == Modes.OFF) {
            return;
        }

        if (!displayInNether && mc.player.dimension == -1)    //turn off in the nether, mobs can spawn no matter what
        {
            return;
        }

        double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTickTime;
        double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTickTime;
        double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTickTime;

        playerPosition = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

        if (safeCalculatorThread == null || !safeCalculatorThread.isAlive()) {
            if (unsafePositions != null)
                unsafePositions.clear();

            if (unsafePositionCache != null && unsafePositions != null)
                unsafePositions = new ArrayList<>(unsafePositionCache);

            safeCalculatorThread = new SafeCalculatorThread(playerPosition);
        }

        if (unsafePositions == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, -z);        //go from cartesian x,y,z coordinates to in-world x,y,z coordinates
        GlStateManager.disableTexture2D();    //fixes color rendering bug (we aren't rendering textures)
        GlStateManager.disableLighting();

        //BLEND and ALPHA allow for color transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (renderUnsafePositionsThroughWalls) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);    //allows this unsafe position to be rendered through other blocks
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        GL11.glBegin(GL11.GL_LINES);    //begin drawing lines defined by 2 vertices

        //render unsafe areas
        for (BlockPos position : unsafePositions) {
            RenderUnsafeMarker(position);
        }
        //GL11.glColor4f(0, 0, 0, 1);    //change alpha back to 100% after we're done rendering
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);    //puts blending back to normal, fixes bad HD texture rendering
        GL11.glDisable(GL11.GL_BLEND);    //fixes [Journeymap] beacons being x-rayed as well
        GL11.glPopMatrix();
    }

    /**
     * Renders an unsafe marker ("X" icon) at the position with colors depending on the Positions light levels.
     * It also takes into account the block above this position and relocates the mark vertically if needed.
     *
     * @param position A position defined by (x,y,z) coordinates
     */
    protected void RenderUnsafeMarker(BlockPos position) {
        IBlockState state = mc.player.world.getBlockState(position);
        Block block = state.getBlock();
        //get bounding box data for this block
        //don't bother for horizontal (X and Z) bounds because every hostile mob spawns on a 1.0 wide block
        //some blocks, like farmland, have a different vertical (Y) bound
        AxisAlignedBB boundingBox = state.getBoundingBox(mc.player.world, position);
        double boundingBoxMinX = 0.0D;
        double boundingBoxMaxX = 1.0D;
        double boundingBoxMaxY = 0.0D;
        if (block instanceof BlockAir) {
            boundingBoxMaxY = 0.0D;
        } else if (boundingBox.maxY < 1.0 &&
                ((boundingBox.maxX - boundingBox.minX) < 1.0 || (boundingBox.maxZ - boundingBox.minZ) < 1.0)) {
            boundingBoxMaxY = 0.0D;
        } else {
            boundingBoxMaxY = boundingBox.maxY == 1.0D ? 0.0 : boundingBox.maxY;
        }
        double boundingBoxMinZ = 0.0D;
        double boundingBoxMaxZ = 1.0D;

        float r, g, b, alpha;
        int lightLevelWithSky = mc.world.getLightFor(EnumSkyBlock.SKY, position);
        int lightLevelWithoutSky = mc.world.getLightFor(EnumSkyBlock.BLOCK, position);

        if (lightLevelWithSky > lightLevelWithoutSky && lightLevelWithSky > 7) {
            //yellow, but decrease the brightness of the "X" marks if the surrounding area is dark
            int blockLightLevel = Math.max(lightLevelWithSky, lightLevelWithoutSky);
            float colorBrightnessModifier = (blockLightLevel) / 15f;

            r = 1f * colorBrightnessModifier;
            g = 1f * colorBrightnessModifier;
            b = 0f;
            alpha = unsafeOverlayTransparency;
        } else {
            //red, but decrease the brightness of the "X" marks if the surrounding area is dark
            int blockLightLevel = Math.max(lightLevelWithSky, lightLevelWithoutSky);
            float colorBrightnessModifier = (blockLightLevel) / 15f + 0.5f;

            r = 0.5f * colorBrightnessModifier;
            g = 0f;
            b = 0f;
            alpha = unsafeOverlayTransparency;
        }

        double minX = position.getX() + boundingBoxMinX + 0.02;
        double maxX = position.getX() + boundingBoxMaxX - 0.02;
        double maxY = position.getY() + boundingBoxMaxY + 0.02;
        double minZ = position.getZ() + boundingBoxMinZ + 0.02;
        double maxZ = position.getZ() + boundingBoxMaxZ - 0.02;

        //render the "X" mark
        //since we are using doubles it causes the marks to 'flicker' when very far from spawn (~5000 blocks)
        //if we use GL11.glVertex3i(int, int, int) it fixes the issue but then we can't render the marks
        //precisely where we want to
        GlStateManager.color(r, g, b, alpha);    //alpha must be > 0.1
        GL11.glVertex3d(maxX, maxY, maxZ);
        GL11.glVertex3d(minX, maxY, minZ);
        GL11.glVertex3d(maxX, maxY, minZ);
        GL11.glVertex3d(minX, maxY, maxZ);
    }

    /**
     * Gets the status of the Safe Overlay
     *
     * @return the string "safe" if the Safe Overlay is enabled, otherwise "".
     */
    public static String CalculateMessageForInfoLine() {
        if (Mode == Modes.OFF || !SafeOverlay.Enabled) {
            return "";
        } else if (Mode == Modes.ON) {
            return TextFormatting.WHITE + Localization.get("safeoverlay.infoline");
        } else {
            return TextFormatting.WHITE + "???";
        }
    }

    /**
     * Gets the current draw distance.
     *
     * @return the draw distance radius
     */
    public int GetDrawDistance() {
        return drawDistance;
    }

    /**
     * Sets the current draw distance.
     *
     * @param newDrawDistance the new draw distance
     * @return the updated draw distance
     */
    public int SetDrawDistance(int newDrawDistance) {
        drawDistance = MathHelper.clamp(newDrawDistance, minDrawDistance, maxDrawDistance);
        return drawDistance;
    }

    /**
     * Increases the current draw distance by 3 blocks.
     *
     * @return the updated draw distance
     */
    public int IncreaseDrawDistance() {
        return SetDrawDistance(drawDistance + 3);
    }

    /**
     * Decreases the current draw distance by 3 blocks.
     *
     * @return the updated draw distance
     */
    public int DecreaseDrawDistance() {
        return SetDrawDistance(drawDistance - 3);
    }

    /**
     * Increases the current draw distance.
     *
     * @param amount how much to increase the draw distance by
     * @return the updated draw distance
     */
    public int IncreaseDrawDistance(int amount) {
        return SetDrawDistance(drawDistance + amount);
    }

    /**
     * Decreases the current draw distance.
     *
     * @param amount how much to increase the draw distance by
     * @return the updated draw distance
     */
    public int DecreaseDrawDistance(int amount) {
        return SetDrawDistance(drawDistance - amount);
    }

    /**
     * Checks if see through walls mode is enabled.
     *
     * @return boolean
     */
    public boolean GetSeeUnsafePositionsThroughWalls() {
        return renderUnsafePositionsThroughWalls;
    }

    /**
     * Sets seeing unsafe areas in the Nether
     *
     * @param displayInUnsafeAreasInNether true or false
     * @return the updated see Nether viewing mode
     */
    public boolean SetDisplayInNether(Boolean displayInUnsafeAreasInNether) {
        return displayInNether = displayInUnsafeAreasInNether;
    }

    /**
     * Gets if you can see unsafe areas in the Nether
     *
     * @return the Nether viewing mode
     */
    public boolean GetDisplayInNether() {
        return displayInNether;
    }

    /**
     * Toggles the current display in Nether mode
     *
     * @return the updated see display in Nether mode
     */
    public boolean ToggleDisplayInNether() {
        return SetDisplayInNether(!displayInNether);
    }

    /**
     * Sets the see through wall mode
     *
     * @param safeOverlaySeeThroughWalls true or false
     * @return the updated see through wall mode
     */
    public boolean SetSeeUnsafePositionsThroughWalls(Boolean safeOverlaySeeThroughWalls) {
        return renderUnsafePositionsThroughWalls = safeOverlaySeeThroughWalls;
    }

    /**
     * Toggles the current see through wall mode
     *
     * @return the udpated see through wall mode
     */
    public boolean ToggleSeeUnsafePositionsThroughWalls() {
        return SetSeeUnsafePositionsThroughWalls(!renderUnsafePositionsThroughWalls);
    }

    /**
     * Sets the alpha value of the unsafe marks
     *
     * @param alpha the alpha value of the unsafe marks, must be between (0.101, 1]
     * @return the updated alpha value
     */
    public float SetUnsafeOverlayTransparency(float alpha) {
        return unsafeOverlayTransparency = MathHelper.clamp(alpha, unsafeOverlayMinTransparency, unsafeOverlayMaxTransparency);
    }

    /**
     * gets the alpha value of the unsafe marks
     *
     * @return the alpha value
     */
    public float GetUnsafeOverlayTransparency() {
        return unsafeOverlayTransparency;
    }

    /**
     * gets the smallest allowed alpha value of the unsafe marks
     *
     * @return the alpha value
     */
    public float GetUnsafeOverlayMinTransparency() {
        return unsafeOverlayMinTransparency;
    }

    /**
     * gets the largest allowed alpha value of the unsafe marks
     *
     * @return the alpha value
     */
    public float GetUnsafeOverlayMaxTransparency() {
        return unsafeOverlayMaxTransparency;
    }
}