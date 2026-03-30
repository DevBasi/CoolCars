package dev.basi.cars;

import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

/**
 * @author basi
 */
public final class CarSettings {

    public enum HealthMode {
        CAR_ONLY,
        PARTS_ONLY,
        CAR_AND_PARTS,
    }

    public static final class DamageSettings {

        public final boolean enabled;
        public final boolean frontEnabled;
        public final boolean rearEnabled;
        public final boolean wheelEnabled;
        public final double frontMaxHealth;
        public final double rearMaxHealth;
        public final double wheelMaxHealth;
        public final double frontImpactMultiplier;
        public final double rearImpactMultiplier;
        public final double wheelImpactMultiplier;
        public final double slowdownStartPercent;
        public final double slowdownMinFactor;
        public final double smokeThresholdPercent;
        public final Vector frontHitboxOffset;
        public final Vector rearHitboxOffset;

        private DamageSettings(
            boolean enabled,
            boolean frontEnabled,
            boolean rearEnabled,
            boolean wheelEnabled,
            double frontMaxHealth,
            double rearMaxHealth,
            double wheelMaxHealth,
            double frontImpactMultiplier,
            double rearImpactMultiplier,
            double wheelImpactMultiplier,
            double slowdownStartPercent,
            double slowdownMinFactor,
            double smokeThresholdPercent,
            Vector frontHitboxOffset,
            Vector rearHitboxOffset
        ) {
            this.enabled = enabled;
            this.frontEnabled = frontEnabled;
            this.rearEnabled = rearEnabled;
            this.wheelEnabled = wheelEnabled;
            this.frontMaxHealth = Math.max(1.0D, frontMaxHealth);
            this.rearMaxHealth = Math.max(1.0D, rearMaxHealth);
            this.wheelMaxHealth = Math.max(1.0D, wheelMaxHealth);
            this.frontImpactMultiplier = Math.max(0.0D, frontImpactMultiplier);
            this.rearImpactMultiplier = Math.max(0.0D, rearImpactMultiplier);
            this.wheelImpactMultiplier = Math.max(0.0D, wheelImpactMultiplier);
            this.slowdownStartPercent = clamp01(slowdownStartPercent);
            this.slowdownMinFactor = clamp01(slowdownMinFactor);
            this.smokeThresholdPercent = clamp01(smokeThresholdPercent);
            this.frontHitboxOffset = frontHitboxOffset.clone();
            this.rearHitboxOffset = rearHitboxOffset.clone();
        }
    }

    public static final class LandingDamageRange {

        public final double minHeight;
        public final double maxHeight;
        public final double frontDamage;
        public final double rearDamage;
        public final double wheelDamage;
        public final double coreDamage;
        public final double frontPercent;
        public final double rearPercent;
        public final double wheelPercent;
        public final double corePercent;

        private LandingDamageRange(
            double minHeight,
            double maxHeight,
            double frontDamage,
            double rearDamage,
            double wheelDamage,
            double coreDamage,
            double frontPercent,
            double rearPercent,
            double wheelPercent,
            double corePercent
        ) {
            this.minHeight = Math.max(0.0D, minHeight);
            this.maxHeight =
                maxHeight < this.minHeight ? this.minHeight : maxHeight;
            this.frontDamage = Math.max(0.0D, frontDamage);
            this.rearDamage = Math.max(0.0D, rearDamage);
            this.wheelDamage = Math.max(0.0D, wheelDamage);
            this.coreDamage = Math.max(0.0D, coreDamage);
            this.frontPercent = Math.max(0.0D, frontPercent);
            this.rearPercent = Math.max(0.0D, rearPercent);
            this.wheelPercent = Math.max(0.0D, wheelPercent);
            this.corePercent = Math.max(0.0D, corePercent);
        }

        public boolean matches(double height) {
            return height >= minHeight && height <= maxHeight;
        }
    }

    public final double mass;
    public final double suspensionRest;
    public final double suspensionStiffness;
    public final double suspensionDamping;
    public final double wheelRadius;
    public final double stepHeight;

    public final double engineForce;
    public final double accelerationForward;
    public final double accelerationReverseMultiplier;
    public final double accelerationCurveExponent;
    public final double brakeForce;
    public final double brakeMultiplier;
    public final double dragCoeff;
    public final double rollingResistance;
    public final double maxSteerRad;
    public final double wheelBase;
    public final double maxReverseSpeed;
    public final double maxForwardSpeed;
    public final double verticalDamping;
    public final double lateralGripGround;
    public final double lateralGripAir;
    public final double maxTickHorizontalDeltaV;

    public final double carHalfWidth;
    public final double carHalfLength;
    public final double carHeight;
    public final double collisionBaseY;
    public final double slideFactor;
    public final double wallDamping;
    public final double unstuckMaxLift;
    public final double unstuckHorizontalNudge;
    public final int unstuckHorizontalAttempts;
    public final double stuckVelocityDamping;

    public final double bodyRollLimitRad;
    public final double bodyRollResponse;
    public final double bodyRollStrength;
    public final double steerResponse;
    public final double steeringInputDeadzone;
    public final double steeringLowInputSteerBoost;
    public final double steeringLowInputResponseBonus;
    public final double steeringLowInputYawBoost;
    public final double steeringHighSpeedReductionStartSpeed;
    public final double steeringHighSpeedReductionEndSpeed;
    public final double steeringHighSpeedMaxReduction;

    public final float interactionWidth;
    public final float interactionHeight;

    public final Vector bodyOffset;
    public final Vector steeringWheelOffset;
    public final Vector[] wheelOffsets;
    public final Vector[] seatOffsets;

    public final int interpolationDuration;
    public final int teleportDuration;

    public final Material bodyMaterial;
    public final Material bodyMaterialHeadlights;
    public final Material wheelMaterial;
    public final Material[] wheelMaterials;
    public final Material steeringWheelMaterial;
    public final int bodyCustomModelData;
    public final int bodyMaterialHeadlightsCustomModelData;
    public final int wheelCustomModelData;
    public final int[] wheelCustomModelDataByIndex;
    public final int steeringWheelCustomModelData;

    public final double maxHealth;
    public final double impactMinDeltaSpeed;
    public final double impactDamageScale;
    public final double impactWallMultiplier;
    public final double idleDamagePerTick;
    public final boolean regenEnabled;
    public final double regenPerTick;
    public final int regenDelayTicks;
    public final double engineDisabledHealthPercent;
    public final boolean destroyOnZeroHealth;
    public final boolean ramDamageEnabled;
    public final double ramDamageMinSpeed;
    public final double ramDamageBase;
    public final double ramDamageSpeedFactor;
    public final double ramDamageMax;
    public final double ramKnockbackHorizontal;
    public final double ramKnockbackVertical;
    public final int ramHitCooldownTicks;
    public final boolean ramAffectPlayers;
    public final boolean ramAffectMobs;
    public final double ramSelfSpeedLossFactor;
    public final boolean landingEnabled;
    public final double landingMinImpactSpeed;
    public final double landingDamageScale;
    public final double landingWheelDamageMultiplier;
    public final double landingBodyDamageMultiplier;
    public final double landingMaxTotalDamage;
    public final boolean landingUseHeightRanges;
    public final boolean landingRequireRangeMatch;
    public final List<LandingDamageRange> landingDamageRanges;
    public final double airPitchVelocityFactor;
    public final double airPitchResponse;
    public final double airRollStabilizeResponse;

    public final double fuelTankCapacity;
    public final double fuelInitialLiters;
    public final double fuelBaseConsumptionPerTick;
    public final double fuelSpeedConsumptionFactor;
    public final double fuelThrottleConsumptionFactor;
    public final double fuelNoFuelBrakeFactor;
    public final double fuelRefuelRateLitersPerTick;
    public final int fuelRefuelSoundIntervalTicks;
    public final Vector fuelPointOffset;
    public final Vector trunkPointOffset;
    public final int trunkSlots;
    public final String trunkTitle;
    public final Vector[] headlightOffsets;
    public final double headlightRange;
    public final int headlightLevel;
    public final int headlightUpdateTicks;
    public final boolean headlightParticlesVisible;
    public final boolean exhaustEnabled;
    public final Vector[] exhaustOffsets;
    public final int exhaustUpdateTicks;
    public final double exhaustMinThrottle;
    public final double exhaustSpeedFactor;
    public final int exhaustBaseCount;
    public final int exhaustMaxCount;
    public final HealthMode healthMode;
    public final double combinedCoreDamageShare;
    public final boolean stopOnCarHealthZero;
    public final boolean stopOnAnyPartZero;
    public final boolean smokeWhenImmobilized;
    public final DamageSettings damage;

    private CarSettings(
        double mass,
        double suspensionRest,
        double suspensionStiffness,
        double suspensionDamping,
        double wheelRadius,
        double stepHeight,
        double engineForce,
        double accelerationForward,
        double accelerationReverseMultiplier,
        double accelerationCurveExponent,
        double brakeForce,
        double brakeMultiplier,
        double dragCoeff,
        double rollingResistance,
        double maxSteerRad,
        double wheelBase,
        double maxReverseSpeed,
        double maxForwardSpeed,
        double verticalDamping,
        double lateralGripGround,
        double lateralGripAir,
        double maxTickHorizontalDeltaV,
        double carHalfWidth,
        double carHalfLength,
        double carHeight,
        double collisionBaseY,
        double slideFactor,
        double wallDamping,
        double unstuckMaxLift,
        double unstuckHorizontalNudge,
        int unstuckHorizontalAttempts,
        double stuckVelocityDamping,
        double bodyRollLimitRad,
        double bodyRollResponse,
        double bodyRollStrength,
        double steerResponse,
        double steeringInputDeadzone,
        double steeringLowInputSteerBoost,
        double steeringLowInputResponseBonus,
        double steeringLowInputYawBoost,
        double steeringHighSpeedReductionStartSpeed,
        double steeringHighSpeedReductionEndSpeed,
        double steeringHighSpeedMaxReduction,
        float interactionWidth,
        float interactionHeight,
        Vector bodyOffset,
        Vector steeringWheelOffset,
        Vector[] wheelOffsets,
        Vector[] seatOffsets,
        int interpolationDuration,
        int teleportDuration,
        Material bodyMaterial,
        Material bodyMaterialHeadlights,
        Material wheelMaterial,
        Material[] wheelMaterials,
        Material steeringWheelMaterial,
        int bodyCustomModelData,
        int bodyMaterialHeadlightsCustomModelData,
        int wheelCustomModelData,
        int[] wheelCustomModelDataByIndex,
        int steeringWheelCustomModelData,
        double maxHealth,
        double impactMinDeltaSpeed,
        double impactDamageScale,
        double impactWallMultiplier,
        double idleDamagePerTick,
        boolean regenEnabled,
        double regenPerTick,
        int regenDelayTicks,
        double engineDisabledHealthPercent,
        boolean destroyOnZeroHealth,
        boolean ramDamageEnabled,
        double ramDamageMinSpeed,
        double ramDamageBase,
        double ramDamageSpeedFactor,
        double ramDamageMax,
        double ramKnockbackHorizontal,
        double ramKnockbackVertical,
        int ramHitCooldownTicks,
        boolean ramAffectPlayers,
        boolean ramAffectMobs,
        double ramSelfSpeedLossFactor,
        boolean landingEnabled,
        double landingMinImpactSpeed,
        double landingDamageScale,
        double landingWheelDamageMultiplier,
        double landingBodyDamageMultiplier,
        double landingMaxTotalDamage,
        boolean landingUseHeightRanges,
        boolean landingRequireRangeMatch,
        List<LandingDamageRange> landingDamageRanges,
        double airPitchVelocityFactor,
        double airPitchResponse,
        double airRollStabilizeResponse,
        double fuelTankCapacity,
        double fuelInitialLiters,
        double fuelBaseConsumptionPerTick,
        double fuelSpeedConsumptionFactor,
        double fuelThrottleConsumptionFactor,
        double fuelNoFuelBrakeFactor,
        double fuelRefuelRateLitersPerTick,
        int fuelRefuelSoundIntervalTicks,
        Vector fuelPointOffset,
        Vector trunkPointOffset,
        int trunkSlots,
        String trunkTitle,
        Vector[] headlightOffsets,
        double headlightRange,
        int headlightLevel,
        int headlightUpdateTicks,
        boolean headlightParticlesVisible,
        boolean exhaustEnabled,
        Vector[] exhaustOffsets,
        int exhaustUpdateTicks,
        double exhaustMinThrottle,
        double exhaustSpeedFactor,
        int exhaustBaseCount,
        int exhaustMaxCount,
        HealthMode healthMode,
        double combinedCoreDamageShare,
        boolean stopOnCarHealthZero,
        boolean stopOnAnyPartZero,
        boolean smokeWhenImmobilized,
        DamageSettings damage
    ) {
        this.mass = mass;
        this.suspensionRest = suspensionRest;
        this.suspensionStiffness = suspensionStiffness;
        this.suspensionDamping = suspensionDamping;
        this.wheelRadius = wheelRadius;
        this.stepHeight = stepHeight;
        this.engineForce = engineForce;
        this.accelerationForward = accelerationForward;
        this.accelerationReverseMultiplier = accelerationReverseMultiplier;
        this.accelerationCurveExponent = accelerationCurveExponent;
        this.brakeForce = brakeForce;
        this.brakeMultiplier = brakeMultiplier;
        this.dragCoeff = dragCoeff;
        this.rollingResistance = rollingResistance;
        this.maxSteerRad = maxSteerRad;
        this.wheelBase = wheelBase;
        this.maxReverseSpeed = maxReverseSpeed;
        this.maxForwardSpeed = maxForwardSpeed;
        this.verticalDamping = verticalDamping;
        this.lateralGripGround = lateralGripGround;
        this.lateralGripAir = lateralGripAir;
        this.maxTickHorizontalDeltaV = maxTickHorizontalDeltaV;
        this.carHalfWidth = carHalfWidth;
        this.carHalfLength = carHalfLength;
        this.carHeight = carHeight;
        this.collisionBaseY = collisionBaseY;
        this.slideFactor = slideFactor;
        this.wallDamping = wallDamping;
        this.unstuckMaxLift = unstuckMaxLift;
        this.unstuckHorizontalNudge = Math.max(0.05D, unstuckHorizontalNudge);
        this.unstuckHorizontalAttempts = Math.max(1, unstuckHorizontalAttempts);
        this.stuckVelocityDamping = clamp01(stuckVelocityDamping);
        this.bodyRollLimitRad = bodyRollLimitRad;
        this.bodyRollResponse = bodyRollResponse;
        this.bodyRollStrength = bodyRollStrength;
        this.steerResponse = steerResponse;
        this.steeringInputDeadzone = Math.max(
            0.0D,
            Math.min(0.25D, steeringInputDeadzone)
        );
        this.steeringLowInputSteerBoost = Math.max(
            1.0D,
            Math.min(1.5D, steeringLowInputSteerBoost)
        );
        this.steeringLowInputResponseBonus = Math.max(
            0.0D,
            Math.min(0.4D, steeringLowInputResponseBonus)
        );
        this.steeringLowInputYawBoost = Math.max(
            1.0D,
            Math.min(1.5D, steeringLowInputYawBoost)
        );
        this.steeringHighSpeedReductionStartSpeed = Math.max(
            0.0D,
            steeringHighSpeedReductionStartSpeed
        );
        this.steeringHighSpeedReductionEndSpeed = Math.max(
            this.steeringHighSpeedReductionStartSpeed + 0.1D,
            steeringHighSpeedReductionEndSpeed
        );
        this.steeringHighSpeedMaxReduction = Math.max(
            0.0D,
            Math.min(0.8D, steeringHighSpeedMaxReduction)
        );
        this.interactionWidth = interactionWidth;
        this.interactionHeight = interactionHeight;
        this.bodyOffset = bodyOffset;
        this.steeringWheelOffset = steeringWheelOffset;
        this.wheelOffsets = wheelOffsets;
        this.seatOffsets = seatOffsets;
        this.interpolationDuration = interpolationDuration;
        this.teleportDuration = teleportDuration;
        this.bodyMaterial = bodyMaterial;
        this.bodyMaterialHeadlights = bodyMaterialHeadlights;
        this.wheelMaterial = wheelMaterial;
        this.wheelMaterials = wheelMaterials.clone();
        this.steeringWheelMaterial = steeringWheelMaterial;
        this.bodyCustomModelData = bodyCustomModelData;
        this.bodyMaterialHeadlightsCustomModelData =
            bodyMaterialHeadlightsCustomModelData;
        this.wheelCustomModelData = wheelCustomModelData;
        this.wheelCustomModelDataByIndex = wheelCustomModelDataByIndex.clone();
        this.steeringWheelCustomModelData = steeringWheelCustomModelData;
        this.maxHealth = maxHealth;
        this.impactMinDeltaSpeed = impactMinDeltaSpeed;
        this.impactDamageScale = impactDamageScale;
        this.impactWallMultiplier = impactWallMultiplier;
        this.idleDamagePerTick = idleDamagePerTick;
        this.regenEnabled = regenEnabled;
        this.regenPerTick = regenPerTick;
        this.regenDelayTicks = regenDelayTicks;
        this.engineDisabledHealthPercent = engineDisabledHealthPercent;
        this.destroyOnZeroHealth = destroyOnZeroHealth;
        this.ramDamageEnabled = ramDamageEnabled;
        this.ramDamageMinSpeed = ramDamageMinSpeed;
        this.ramDamageBase = ramDamageBase;
        this.ramDamageSpeedFactor = ramDamageSpeedFactor;
        this.ramDamageMax = ramDamageMax;
        this.ramKnockbackHorizontal = ramKnockbackHorizontal;
        this.ramKnockbackVertical = ramKnockbackVertical;
        this.ramHitCooldownTicks = ramHitCooldownTicks;
        this.ramAffectPlayers = ramAffectPlayers;
        this.ramAffectMobs = ramAffectMobs;
        this.ramSelfSpeedLossFactor = ramSelfSpeedLossFactor;
        this.landingEnabled = landingEnabled;
        this.landingMinImpactSpeed = Math.max(0.0D, landingMinImpactSpeed);
        this.landingDamageScale = Math.max(0.0D, landingDamageScale);
        this.landingWheelDamageMultiplier = Math.max(
            0.0D,
            landingWheelDamageMultiplier
        );
        this.landingBodyDamageMultiplier = Math.max(
            0.0D,
            landingBodyDamageMultiplier
        );
        this.landingMaxTotalDamage = Math.max(0.0D, landingMaxTotalDamage);
        this.landingUseHeightRanges = landingUseHeightRanges;
        this.landingRequireRangeMatch = landingRequireRangeMatch;
        this.landingDamageRanges =
            landingDamageRanges == null
                ? List.of()
                : List.copyOf(landingDamageRanges);
        this.airPitchVelocityFactor = Math.max(0.0D, airPitchVelocityFactor);
        this.airPitchResponse = clamp01(airPitchResponse);
        this.airRollStabilizeResponse = clamp01(airRollStabilizeResponse);
        this.fuelTankCapacity = fuelTankCapacity;
        this.fuelInitialLiters = fuelInitialLiters;
        this.fuelBaseConsumptionPerTick = fuelBaseConsumptionPerTick;
        this.fuelSpeedConsumptionFactor = fuelSpeedConsumptionFactor;
        this.fuelThrottleConsumptionFactor = fuelThrottleConsumptionFactor;
        this.fuelNoFuelBrakeFactor = fuelNoFuelBrakeFactor;
        this.fuelRefuelRateLitersPerTick = fuelRefuelRateLitersPerTick;
        this.fuelRefuelSoundIntervalTicks = fuelRefuelSoundIntervalTicks;
        this.fuelPointOffset = fuelPointOffset;
        this.trunkPointOffset = trunkPointOffset;
        this.trunkSlots = trunkSlots;
        this.trunkTitle = trunkTitle;
        this.headlightOffsets = cloneVectors(headlightOffsets);
        this.headlightRange = headlightRange;
        this.headlightLevel = headlightLevel;
        this.headlightUpdateTicks = headlightUpdateTicks;
        this.headlightParticlesVisible = headlightParticlesVisible;
        this.exhaustEnabled = exhaustEnabled;
        this.exhaustOffsets = cloneVectors(exhaustOffsets);
        this.exhaustUpdateTicks = exhaustUpdateTicks;
        this.exhaustMinThrottle = exhaustMinThrottle;
        this.exhaustSpeedFactor = exhaustSpeedFactor;
        this.exhaustBaseCount = exhaustBaseCount;
        this.exhaustMaxCount = exhaustMaxCount;
        this.healthMode =
            healthMode == null ? HealthMode.PARTS_ONLY : healthMode;
        this.combinedCoreDamageShare = Math.max(
            0.0D,
            Math.min(1.0D, combinedCoreDamageShare)
        );
        this.stopOnCarHealthZero = stopOnCarHealthZero;
        this.stopOnAnyPartZero = stopOnAnyPartZero;
        this.smokeWhenImmobilized = smokeWhenImmobilized;
        this.damage = damage;
    }

    public static CarSettings fromConfig(FileConfiguration cfg) {
        Vector defaultBodyOffset = new Vector(0.0D, 0.82D, 0.0D);
        Vector defaultSteeringWheelOffset = new Vector(-0.16D, 1.06D, 0.92D);
        Vector defaultFuelPointOffset = new Vector(0.0D, 0.60D, -1.95D);
        Vector defaultTrunkPointOffset = new Vector(0.0D, 0.95D, -2.05D);
        Vector[] defaultWheelOffsets = new Vector[] {
            new Vector(-0.88D, 0.35D, 1.28D),
            new Vector(0.88D, 0.35D, 1.28D),
            new Vector(-0.88D, 0.35D, -1.28D),
            new Vector(0.88D, 0.35D, -1.28D),
        };
        Vector[] defaultSeatOffsets = new Vector[] {
            new Vector(-0.35D, 0.63D, 0.54D),
            new Vector(0.35D, 0.63D, 0.54D),
            new Vector(-0.32D, 0.63D, -0.62D),
            new Vector(0.32D, 0.63D, -0.62D),
        };

        Vector bodyOffset = readVector(
            cfg,
            "car.visual.body-offset",
            defaultBodyOffset
        );
        Vector steeringWheelOffset = readVector(
            cfg,
            "car.visual.steering-wheel-offset",
            defaultSteeringWheelOffset
        );
        Vector fuelPointOffset = readVector(
            cfg,
            "car.fuel.point-offset",
            defaultFuelPointOffset
        );
        Vector trunkPointOffset = readVector(
            cfg,
            "car.trunk.point-offset",
            defaultTrunkPointOffset
        );
        Vector[] wheelOffsets = readVectorList(
            cfg,
            "car.visual.wheel-offsets",
            defaultWheelOffsets,
            4
        );
        Vector[] seatOffsets = readVectorList(
            cfg,
            "car.seats.offsets",
            defaultSeatOffsets,
            4
        );

        Material bodyMaterial = parseMaterial(
            cfg.getString(
                "car.models.body-material",
                "IRON_INGOT"
            ),
            Material.IRON_INGOT
        );
        Material bodyMaterialHeadlights = parseMaterial(
            cfg.getString(
                "car.models.body-material-headlights",
                bodyMaterial.name()
            ),
            bodyMaterial
        );
        Material wheelMaterial = parseMaterial(
            cfg.getString(
                "car.models.wheel-material",
                "IRON_INGOT"
            ),
            Material.IRON_INGOT
        );
        String wheelDefaultName = cfg.getString(
            "car.models.wheels.default",
            wheelMaterial.name()
        );
        Material wheelDefault = parseMaterial(wheelDefaultName, wheelMaterial);
        Material wheelFrontLeft = parseMaterial(
            cfg.getString(
                "car.models.wheels.front-left",
                wheelDefault.name()
            ),
            wheelDefault
        );
        Material wheelFrontRight = parseMaterial(
            cfg.getString(
                "car.models.wheels.front-right",
                wheelDefault.name()
            ),
            wheelDefault
        );
        Material wheelRearLeft = parseMaterial(
            cfg.getString(
                "car.models.wheels.rear-left",
                wheelDefault.name()
            ),
            wheelDefault
        );
        Material wheelRearRight = parseMaterial(
            cfg.getString(
                "car.models.wheels.rear-right",
                wheelDefault.name()
            ),
            wheelDefault
        );
        Material[] wheelMaterials = new Material[] {
            wheelFrontLeft,
            wheelFrontRight,
            wheelRearLeft,
            wheelRearRight,
        };
        Material steeringWheelMaterial = parseMaterial(
            cfg.getString(
                "car.models.steering-wheel-material",
                "IRON_INGOT"
            ),
            Material.IRON_INGOT
        );
        int bodyCustomModelData = parseOptionalInt(
            cfg,
            "car.models.body-custom-model-data",
            -1
        );
        int bodyMaterialHeadlightsCustomModelData = parseOptionalInt(
            cfg,
            "car.models.body-material-headlights-custom-model-data",
            bodyCustomModelData
        );
        int wheelCustomModelData = parseOptionalInt(
            cfg,
            "car.models.wheel-custom-model-data",
            -1
        );
        int wheelDefaultCustomModelData = parseOptionalInt(
            cfg,
            "car.models.wheels.default-custom-model-data",
            wheelCustomModelData
        );
        int[] wheelCustomModelDataByIndex = new int[] {
            parseOptionalInt(
                cfg,
                "car.models.wheels.front-left-custom-model-data",
                wheelDefaultCustomModelData
            ),
            parseOptionalInt(
                cfg,
                "car.models.wheels.front-right-custom-model-data",
                wheelDefaultCustomModelData
            ),
            parseOptionalInt(
                cfg,
                "car.models.wheels.rear-left-custom-model-data",
                wheelDefaultCustomModelData
            ),
            parseOptionalInt(
                cfg,
                "car.models.wheels.rear-right-custom-model-data",
                wheelDefaultCustomModelData
            ),
        };
        int steeringWheelCustomModelData = parseOptionalInt(
            cfg,
            "car.models.steering-wheel-custom-model-data",
            -1
        );
        Vector[] defaultHeadlightOffsets = new Vector[] {
            new Vector(-0.46D, 0.78D, 1.72D),
            new Vector(0.46D, 0.78D, 1.72D),
        };
        Vector[] headlightOffsets = readVectorList(
            cfg,
            "car.lights.headlight-offsets",
            defaultHeadlightOffsets,
            2
        );
        Vector[] defaultExhaustOffsets = new Vector[] {
            new Vector(-0.34D, 0.48D, -1.95D),
            new Vector(0.34D, 0.48D, -1.95D),
        };
        Vector[] exhaustOffsets = readVectorList(
            cfg,
            "car.exhaust.offsets",
            defaultExhaustOffsets,
            2
        );
        DamageSettings damage = new DamageSettings(
            cfg.getBoolean(
                "car.damage.enabled",
                true
            ),
            cfg.getBoolean(
                "car.damage.parts.front.enabled",
                true
            ),
            cfg.getBoolean(
                "car.damage.parts.rear.enabled",
                true
            ),
            cfg.getBoolean(
                "car.damage.parts.wheels.enabled",
                true
            ),
            cfg.getDouble(
                "car.damage.parts.front.max-health",
                350.0D
            ),
            cfg.getDouble(
                "car.damage.parts.rear.max-health",
                300.0D
            ),
            cfg.getDouble(
                "car.damage.parts.wheels.max-health",
                120.0D
            ),
            cfg.getDouble(
                "car.damage.impact.front-multiplier",
                1.20D
            ),
            cfg.getDouble(
                "car.damage.impact.rear-multiplier",
                1.0D
            ),
            cfg.getDouble(
                "car.damage.impact.wheel-multiplier",
                0.45D
            ),
            cfg.getDouble(
                "car.damage.performance.slowdown-start-percent",
                0.35D
            ),
            cfg.getDouble(
                "car.damage.performance.min-factor-at-zero",
                0.35D
            ),
            cfg.getDouble(
                "car.damage.visual.smoke-threshold-percent",
                0.20D
            ),
            readVector(
                cfg,
                "car.damage.hitbox.front-offset",
                new Vector(0.0D, 0.75D, 1.95D)
            ),
            readVector(
                cfg,
                "car.damage.hitbox.rear-offset",
                new Vector(0.0D, 0.75D, -2.05D)
            )
        );
        List<LandingDamageRange> landingDamageRanges = readLandingDamageRanges(
            cfg,
            "car.health.landing.height-ranges"
        );

        return new CarSettings(
            cfg.getDouble(
                "car.physics.mass",
                1250.0D
            ),
            cfg.getDouble(
                "car.suspension.rest-length",
                0.55D
            ),
            cfg.getDouble(
                "car.suspension.stiffness",
                21000.0D
            ),
            cfg.getDouble(
                "car.suspension.damping",
                2100.0D
            ),
            cfg.getDouble(
                "car.suspension.wheel-radius",
                0.36D
            ),
            cfg.getDouble(
                "car.suspension.step-height",
                1.0D
            ),
            cfg.getDouble(
                "car.drivetrain.engine-force",
                9700.0D
            ),
            cfg.getDouble(
                "car.drivetrain.acceleration-forward",
                1.0D
            ),
            cfg.getDouble(
                "car.drivetrain.acceleration-reverse-multiplier",
                0.58D
            ),
            cfg.getDouble(
                "car.drivetrain.acceleration-curve-exponent",
                1.0D
            ),
            cfg.getDouble(
                "car.drivetrain.brake-force",
                15000.0D
            ),
            cfg.getDouble(
                "car.drivetrain.brake-multiplier",
                1.0D
            ),
            cfg.getDouble(
                "car.physics.drag-coeff",
                0.018D
            ),
            cfg.getDouble(
                "car.physics.rolling-resistance",
                55.0D
            ),
            Math.toRadians(
                cfg.getDouble(
                    "car.steering.max-steer-deg",
                    28.0D
                )
            ),
            cfg.getDouble(
                "car.steering.wheel-base",
                2.65D
            ),
            cfg.getDouble(
                "car.drivetrain.max-reverse-speed",
                9.5D
            ),
            cfg.getDouble(
                "car.drivetrain.max-forward-speed",
                34.0D
            ),
            cfg.getDouble(
                "car.physics.vertical-damping",
                3.8D
            ),
            cfg.getDouble(
                "car.physics.lateral-grip-ground",
                5.8D
            ),
            cfg.getDouble(
                "car.physics.lateral-grip-air",
                0.9D
            ),
            cfg.getDouble(
                "car.physics.max-tick-horizontal-delta-v",
                1.10D
            ),
            cfg.getDouble(
                "car.collision.half-width",
                0.95D
            ),
            cfg.getDouble(
                "car.collision.half-length",
                1.40D
            ),
            cfg.getDouble(
                "car.collision.height",
                1.45D
            ),
            cfg.getDouble(
                "car.collision.base-y",
                0.32D
            ),
            cfg.getDouble(
                "car.collision.slide-factor",
                0.92D
            ),
            cfg.getDouble(
                "car.collision.wall-damping",
                0.90D
            ),
            cfg.getDouble(
                "car.collision.unstuck-max-lift",
                1.2D
            ),
            cfg.getDouble(
                "car.collision.unstuck-horizontal-nudge",
                0.28D
            ),
            cfg.getInt(
                "car.collision.unstuck-horizontal-attempts",
                6
            ),
            cfg.getDouble(
                "car.collision.stuck-velocity-damping",
                0.82D
            ),
            Math.toRadians(
                cfg.getDouble(
                    "car.steering.body-roll-limit-deg",
                    10.0D
                )
            ),
            cfg.getDouble(
                "car.steering.body-roll-response",
                0.18D
            ),
            Math.max(
                0.1D,
                cfg.getDouble(
                    "car.steering.body-roll-strength",
                    1.0D
                )
            ),
            cfg.getDouble(
                "car.steering.steer-response",
                0.35D
            ),
            cfg.getDouble(
                "car.steering.tuning.input-deadzone",
                0.06D
            ),
            cfg.getDouble(
                "car.steering.tuning.low-input-steer-boost",
                1.04D
            ),
            cfg.getDouble(
                "car.steering.tuning.low-input-response-bonus",
                0.02D
            ),
            cfg.getDouble(
                "car.steering.tuning.low-input-yaw-boost",
                1.03D
            ),
            cfg.getDouble(
                "car.steering.tuning.high-speed-reduction-start-speed",
                8.0D
            ),
            cfg.getDouble(
                "car.steering.tuning.high-speed-reduction-end-speed",
                34.0D
            ),
            cfg.getDouble(
                "car.steering.tuning.high-speed-max-reduction",
                0.42D
            ),
            (float) cfg.getDouble(
                "car.interaction.hitbox-width",
                2.3D
            ),
            (float) cfg.getDouble(
                "car.interaction.hitbox-height",
                1.8D
            ),
            bodyOffset,
            steeringWheelOffset,
            wheelOffsets,
            seatOffsets,
            Math.max(
                2,
                cfg.getInt(
                    "car.visual.interpolation-duration",
                    2
                )
            ),
            Math.max(
                2,
                cfg.getInt(
                    "car.visual.teleport-duration",
                    2
                )
            ),
            bodyMaterial,
            bodyMaterialHeadlights,
            wheelMaterial,
            wheelMaterials,
            steeringWheelMaterial,
            bodyCustomModelData,
            bodyMaterialHeadlightsCustomModelData,
            wheelCustomModelData,
            wheelCustomModelDataByIndex,
            steeringWheelCustomModelData,
            cfg.getDouble(
                "car.health.max",
                1000.0D
            ),
            cfg.getDouble(
                "car.health.impact.min-delta-speed",
                4.0D
            ),
            cfg.getDouble(
                "car.health.impact.damage-scale",
                12.0D
            ),
            cfg.getDouble(
                "car.health.impact.wall-multiplier",
                1.15D
            ),
            cfg.getDouble(
                "car.health.idle-damage-per-tick",
                0.0D
            ),
            cfg.getBoolean(
                "car.health.regen.enabled",
                true
            ),
            cfg.getDouble(
                "car.health.regen.per-tick",
                0.20D
            ),
            cfg.getInt(
                "car.health.regen.delay-ticks",
                80
            ),
            clamp01(
                cfg.getDouble(
                    "car.health.engine-disabled-health-percent",
                    0.10D
                )
            ),
            cfg.getBoolean(
                "car.health.destroy-on-zero",
                true
            ),
            cfg.getBoolean(
                "car.combat.ram.enabled",
                true
            ),
            cfg.getDouble(
                "car.combat.ram.min-speed",
                7.0D
            ),
            cfg.getDouble(
                "car.combat.ram.base-damage",
                2.0D
            ),
            cfg.getDouble(
                "car.combat.ram.speed-damage-factor",
                0.55D
            ),
            cfg.getDouble(
                "car.combat.ram.max-damage",
                20.0D
            ),
            cfg.getDouble(
                "car.combat.ram.knockback-horizontal",
                1.15D
            ),
            cfg.getDouble(
                "car.combat.ram.knockback-vertical",
                0.25D
            ),
            Math.max(
                0,
                cfg.getInt(
                    "car.combat.ram.hit-cooldown-ticks",
                    10
                )
            ),
            cfg.getBoolean(
                "car.combat.ram.affect-players",
                false
            ),
            cfg.getBoolean(
                "car.combat.ram.affect-mobs",
                true
            ),
            clamp01(
                cfg.getDouble(
                    "car.combat.ram.self-speed-loss-factor",
                    0.92D
                )
            ),
            cfg.getBoolean(
                "car.health.landing.enabled",
                true
            ),
            cfg.getDouble(
                "car.health.landing.min-impact-speed",
                6.5D
            ),
            cfg.getDouble(
                "car.health.landing.damage-scale",
                22.0D
            ),
            cfg.getDouble(
                "car.health.landing.wheel-damage-multiplier",
                1.15D
            ),
            cfg.getDouble(
                "car.health.landing.body-damage-multiplier",
                0.55D
            ),
            cfg.getDouble("car.health.landing.max-total-damage", 180.0D),
            cfg.getBoolean("car.health.landing.use-height-ranges", false),
            cfg.getBoolean("car.health.landing.range-require-match", false),
            landingDamageRanges,
            cfg.getDouble(
                "car.physics.air-control.pitch-velocity-factor",
                0.85D
            ),
            cfg.getDouble(
                "car.physics.air-control.pitch-response",
                0.12D
            ),
            cfg.getDouble(
                "car.physics.air-control.roll-stabilize-response",
                0.08D
            ),
            cfg.getDouble(
                "car.fuel.tank-capacity",
                60.0D
            ),
            cfg.getDouble(
                "car.fuel.initial-fuel",
                35.0D
            ),
            cfg.getDouble(
                "car.fuel.consumption.base-per-tick",
                0.0008D
            ),
            cfg.getDouble(
                "car.fuel.consumption.speed-factor",
                0.00020D
            ),
            cfg.getDouble(
                "car.fuel.consumption.throttle-factor",
                0.00120D
            ),
            cfg.getDouble(
                "car.fuel.no-fuel-brake-factor",
                0.93D
            ),
            cfg.getDouble(
                "car.fuel.refuel-rate-liters-per-tick",
                0.10D
            ),
            cfg.getInt(
                "car.fuel.refuel-sound-interval-ticks",
                6
            ),
            fuelPointOffset,
            trunkPointOffset,
            normalizeInventorySize(
                cfg.getInt("car.trunk.slots", 18)
            ),
            cfg.getString(
                "car.trunk.title",
                "Car Trunk"
            ),
            headlightOffsets,
            Math.max(
                2.0D,
                cfg.getDouble(
                    "car.lights.range",
                    12.0D
                )
            ),
            Math.max(
                1,
                Math.min(
                    15,
                    cfg.getInt(
                        "car.lights.level",
                        15
                    )
                )
            ),
            Math.max(
                1,
                cfg.getInt(
                    "car.lights.update-ticks",
                    2
                )
            ),
            cfg.getBoolean(
                "car.lights.visible-effects",
                false
            ),
            cfg.getBoolean(
                "car.exhaust.enabled",
                true
            ),
            exhaustOffsets,
            Math.max(
                1,
                cfg.getInt(
                    "car.exhaust.update-ticks",
                    2
                )
            ),
            Math.max(
                0.0D,
                cfg.getDouble(
                    "car.exhaust.min-throttle",
                    0.12D
                )
            ),
            Math.max(
                0.0D,
                cfg.getDouble(
                    "car.exhaust.speed-factor",
                    0.14D
                )
            ),
            Math.max(
                0,
                cfg.getInt(
                    "car.exhaust.base-count",
                    1
                )
            ),
            Math.max(
                1,
                cfg.getInt(
                    "car.exhaust.max-count",
                    8
                )
            ),
            parseHealthMode(
                cfg.getString("car.health.system.mode", "parts_only")
            ),
            cfg.getDouble(
                "car.health.system.combined-core-damage-share",
                0.50D
            ),
            cfg.getBoolean("car.health.system.stop-on-car-health-zero", true),
            cfg.getBoolean("car.health.system.stop-on-any-part-zero", true),
            cfg.getBoolean("car.health.system.smoke-when-immobilized", true),
            damage
        );
    }

    private static Vector readVector(
        FileConfiguration cfg,
        String path,
        Vector def
    ) {
        ConfigurationSection section = cfg.getConfigurationSection(path);
        if (section == null) {
            return def.clone();
        }
        return new Vector(
            section.getDouble("x", def.getX()),
            section.getDouble("y", def.getY()),
            section.getDouble("z", def.getZ())
        );
    }

    private static Vector[] readVectorList(
        FileConfiguration cfg,
        String path,
        Vector[] def,
        int requiredSize
    ) {
        List<Map<?, ?>> raw = cfg.getMapList(path);
        if (raw.isEmpty()) {
            return cloneVectors(def);
        }

        Vector[] out = new Vector[requiredSize];
        for (int i = 0; i < requiredSize; i++) {
            if (i < raw.size()) {
                out[i] = fromMap(raw.get(i), def[Math.min(i, def.length - 1)]);
            } else {
                out[i] = def[Math.min(i, def.length - 1)].clone();
            }
        }
        return out;
    }

    private static Vector fromMap(Map<?, ?> map, Vector def) {
        return new Vector(
            asDouble(map.get("x"), def.getX()),
            asDouble(map.get("y"), def.getY()),
            asDouble(map.get("z"), def.getZ())
        );
    }

    private static double asDouble(Object value, double def) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return def;
            }
        }
        return def;
    }

    private static Vector[] cloneVectors(Vector[] source) {
        Vector[] out = new Vector[source.length];
        for (int i = 0; i < source.length; i++) {
            out[i] = source[i].clone();
        }
        return out;
    }

    public LandingDamageRange findLandingDamageRange(double height) {
        if (landingDamageRanges.isEmpty()) {
            return null;
        }
        for (LandingDamageRange range : landingDamageRanges) {
            if (range != null && range.matches(height)) {
                return range;
            }
        }
        return null;
    }

    private static List<LandingDamageRange> readLandingDamageRanges(
        FileConfiguration cfg,
        String path
    ) {
        List<Map<?, ?>> raw = cfg.getMapList(path);
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<LandingDamageRange> out = new java.util.ArrayList<>();
        for (Map<?, ?> row : raw) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            double minHeight = asDouble(row.get("min-height"), 0.0D);
            double maxHeight = asDouble(
                row.get("max-height"),
                Double.POSITIVE_INFINITY
            );
            double frontDamage = asDouble(row.get("front-damage"), 0.0D);
            double rearDamage = asDouble(row.get("rear-damage"), 0.0D);
            double wheelDamage = asDouble(row.get("wheel-damage"), 0.0D);
            double coreDamage = asDouble(row.get("core-damage"), 0.0D);
            double frontPercent = asDouble(row.get("front-percent"), 0.0D);
            double rearPercent = asDouble(row.get("rear-percent"), 0.0D);
            double wheelPercent = asDouble(row.get("wheel-percent"), 0.0D);
            double corePercent = asDouble(row.get("core-percent"), 0.0D);
            out.add(
                new LandingDamageRange(
                    minHeight,
                    maxHeight,
                    frontDamage,
                    rearDamage,
                    wheelDamage,
                    coreDamage,
                    frontPercent,
                    rearPercent,
                    wheelPercent,
                    corePercent
                )
            );
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    private static Material parseMaterial(String name, Material fallback) {
        Material parsed = Material.matchMaterial(
            name == null ? "" : name
        );
        return parsed == null ? fallback : parsed;
    }

    private static int parseOptionalInt(
        FileConfiguration cfg,
        String path,
        int fallback
    ) {
        return cfg.isSet(path) ? cfg.getInt(path) : fallback;
    }

    private static double clamp01(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static HealthMode parseHealthMode(String raw) {
        if (raw == null || raw.isBlank()) {
            return HealthMode.PARTS_ONLY;
        }
        String normalized = raw.trim().toLowerCase(java.util.Locale.ROOT);
        return switch (normalized) {
            case "car_only", "car-only", "car" -> HealthMode.CAR_ONLY;
            case
                "car_and_parts",
                "car-and-parts",
                "combined",
                "both" -> HealthMode.CAR_AND_PARTS;
            default -> HealthMode.PARTS_ONLY;
        };
    }

    private static int normalizeInventorySize(int raw) {
        int clamped = Math.max(9, Math.min(54, raw));
        int mod = clamped % 9;
        return mod == 0 ? clamped : clamped + (9 - mod);
    }
}
