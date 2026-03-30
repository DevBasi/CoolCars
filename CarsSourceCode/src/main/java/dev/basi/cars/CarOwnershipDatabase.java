package dev.basi.cars;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author basi
 */
public final class CarOwnershipDatabase {

    private static final String DRIVER_SQLITE = "org.sqlite.JDBC";
    private static final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_SQLITE_PREFIX = "jdbc:sqlite:";
    private static final String JDBC_MYSQL_PREFIX = "jdbc:mysql://";
    private static final String MYSQL_PARAMS_DEFAULT = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public enum Mode {
        DISABLED,
        SQLITE,
        MYSQL,
    }

    public static final class OwnershipRecord {

        public final UUID ownerId;
        public final String ownerName;

        private OwnershipRecord(UUID ownerId, String ownerName) {
            this.ownerId = ownerId;
            this.ownerName = ownerName;
        }
    }

    public static final class TelemetryStateRecord {

        public final long firstSeenAt;
        public final long spawnedAt;
        public final long lastUsedAt;
        public final long lastSeenAt;
        public final long lastEventAt;
        public final String lastEvent;
        public final String lastEventDetails;

        private TelemetryStateRecord(
            long firstSeenAt,
            long spawnedAt,
            long lastUsedAt,
            long lastSeenAt,
            long lastEventAt,
            String lastEvent,
            String lastEventDetails
        ) {
            this.firstSeenAt = firstSeenAt;
            this.spawnedAt = spawnedAt;
            this.lastUsedAt = lastUsedAt;
            this.lastSeenAt = lastSeenAt;
            this.lastEventAt = lastEventAt;
            this.lastEvent = lastEvent;
            this.lastEventDetails = lastEventDetails;
        }
    }

    public static final class TelemetrySnapshot {

        public UUID vehicleId;
        public String modelKey;
        public String modelName;
        public UUID ownerId;
        public String ownerName;
        public long firstSeenAt;
        public long spawnedAt;
        public long lastUsedAt;
        public long lastSeenAt;
        public long lastEventAt;
        public String lastEvent;
        public String lastEventDetails;
        public String worldName;
        public double x;
        public double y;
        public double z;
        public float yaw;
        public float pitch;
        public double health;
        public double healthMax;
        public double healthPercent;
        public double fuel;
        public double fuelMax;
        public double fuelPercent;
        public boolean engineRunning;
        public boolean headlightsOn;
        public double speedMps;
        public int occupiedSeats;
        public int totalSeats;
        public double damageFront;
        public double damageRear;
        public double damageWheelFl;
        public double damageWheelFr;
        public double damageWheelRl;
        public double damageWheelRr;
        public String damageSnapshot;
    }

    private final JavaPlugin plugin;

    private Mode mode = Mode.DISABLED;
    private boolean enabled;
    private String jdbcUrl;
    private String username;
    private String password;
    private int loginTimeoutSeconds = 5;
    private boolean telemetryEnabled = true;
    private boolean telemetryEventsEnabled = true;
    private int telemetryEventDetailsLimit = 1024;
    private int telemetryModelKeyLimit = 96;
    private int telemetryModelNameLimit = 128;
    private int telemetryOwnerNameLimit = 64;
    private int telemetryEventTypeLimit = 64;

    public CarOwnershipDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reloadFromConfig(FileConfiguration cfg) {
        this.enabled = cfg.getBoolean(
            "database.enabled",
            true
        );
        if (!enabled) {
            this.mode = Mode.DISABLED;
            this.jdbcUrl = null;
            this.username = null;
            this.password = null;
            plugin
                .getLogger()
                .info(
                    "Car ownership database disabled in config."
                );
            return;
        }

        String type = cfg
            .getString(
                "database.type",
                "sqlite"
            )
            .trim()
            .toLowerCase();
        this.loginTimeoutSeconds = Math.max(
            2,
            cfg.getInt(
                "database.connection-timeout-seconds",
                5
            )
        );
        this.telemetryEnabled = cfg.getBoolean(
            "database.telemetry.enabled",
            true
        );
        this.telemetryEventsEnabled = cfg.getBoolean(
            "database.telemetry.events.enabled",
            true
        );
        this.telemetryEventDetailsLimit = Math.max(
            128,
            cfg.getInt("database.telemetry.events.max-details-length", 1024)
        );
        this.telemetryModelKeyLimit = Math.max(
            32,
            cfg.getInt("database.telemetry.max-model-key-length", 96)
        );
        this.telemetryModelNameLimit = Math.max(
            32,
            cfg.getInt("database.telemetry.max-model-name-length", 128)
        );
        this.telemetryOwnerNameLimit = Math.max(
            16,
            cfg.getInt("database.telemetry.max-owner-name-length", 64)
        );
        this.telemetryEventTypeLimit = Math.max(
            16,
            cfg.getInt("database.telemetry.events.max-type-length", 64)
        );

        try {
            if (
                type.equals("mysql") ||
                type.equals("mariadb")
            ) {
                this.mode = Mode.MYSQL;
                configureMySql(cfg);
            } else {
                this.mode = Mode.SQLITE;
                configureSqlite(cfg);
            }

            initSchema();
            plugin
                .getLogger()
                .info(
                    "Car ownership database is ready (" +
                        mode.name() +
                        ")."
                );
        } catch (Exception ex) {
            this.mode = Mode.DISABLED;
            this.enabled = false;
            plugin
                .getLogger()
                .warning(
                    "Failed to initialize car ownership database: " +
                        ex.getMessage()
                );
        }
    }

    public boolean isEnabled() {
        return enabled && mode != Mode.DISABLED && jdbcUrl != null;
    }

    public Map<UUID, OwnershipRecord> loadOwnershipRecords() {
        Map<UUID, OwnershipRecord> out = new HashMap<>();
        if (!isEnabled()) {
            return out;
        }

        String sql = "SELECT vehicle_id, owner_uuid, owner_name FROM player_car_ownership";
        try (
            Connection connection = openConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                UUID vehicleId = parseUuid(
                    rs.getString("vehicle_id")
                );
                UUID ownerId = parseUuid(
                    rs.getString("owner_uuid")
                );
                if (vehicleId == null || ownerId == null) {
                    continue;
                }
                out.put(
                    vehicleId,
                    new OwnershipRecord(
                        ownerId,
                        rs.getString(
                            "owner_name"
                        )
                    )
                );
            }
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Failed to load ownership records: " +
                        ex.getMessage()
                );
        }
        return out;
    }

    public boolean upsertOwnership(
        UUID vehicleId,
        UUID ownerId,
        String ownerName,
        Location location,
        double health,
        double fuel,
        boolean engineRunning,
        boolean headlightsOn
    ) {
        if (!isEnabled() || vehicleId == null || ownerId == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        String worldName =
            location != null && location.getWorld() != null
                ? location.getWorld().getName()
                : null;
        double x = location != null ? location.getX() : 0.0D;
        double y = location != null ? location.getY() : 0.0D;
        double z = location != null ? location.getZ() : 0.0D;
        float yaw = location != null ? location.getYaw() : 0.0F;

        String sql = buildUpsertSql();
        try (
            Connection connection = openConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, vehicleId.toString());
            statement.setString(2, ownerId.toString());
            statement.setString(
                3,
                ownerName == null ? "" : ownerName
            );
            statement.setLong(4, now);
            statement.setLong(5, now);
            statement.setString(6, worldName);
            statement.setDouble(7, x);
            statement.setDouble(8, y);
            statement.setDouble(9, z);
            statement.setFloat(10, yaw);
            statement.setDouble(11, health);
            statement.setDouble(12, fuel);
            statement.setBoolean(13, engineRunning);
            statement.setBoolean(14, headlightsOn);
            statement.executeUpdate();
            return true;
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Failed to upsert ownership record for " +
                        vehicleId +
                        ": " +
                        ex.getMessage()
                );
            return false;
        }
    }

    public Map<UUID, TelemetryStateRecord> loadTelemetryStateRecords() {
        Map<UUID, TelemetryStateRecord> out = new HashMap<>();
        if (!isEnabled() || !telemetryEnabled) {
            return out;
        }
        String sql =
            "SELECT vehicle_id, first_seen_at, spawned_at, last_used_at, " +
            "last_seen_at, last_event_at, last_event, last_event_details " +
            "FROM car_telemetry_state";
        try (
            Connection connection = openConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                UUID vehicleId = parseUuid(rs.getString("vehicle_id"));
                if (vehicleId == null) {
                    continue;
                }
                out.put(
                    vehicleId,
                    new TelemetryStateRecord(
                        rs.getLong("first_seen_at"),
                        rs.getLong("spawned_at"),
                        rs.getLong("last_used_at"),
                        rs.getLong("last_seen_at"),
                        rs.getLong("last_event_at"),
                        rs.getString("last_event"),
                        rs.getString("last_event_details")
                    )
                );
            }
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning("Failed to load telemetry state: " + ex.getMessage());
        }
        return out;
    }

    public boolean upsertTelemetryState(TelemetrySnapshot snapshot) {
        if (
            !isEnabled() ||
            !telemetryEnabled ||
            snapshot == null ||
            snapshot.vehicleId == null
        ) {
            return false;
        }
        String sql =
            mode == Mode.MYSQL
                ? buildMySqlTelemetryUpsert()
                : buildSqliteTelemetryUpsert();
        long now = System.currentTimeMillis();
        try (
            Connection connection = openConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            int i = 1;
            statement.setString(i++, snapshot.vehicleId.toString());
            statement.setString(
                i++,
                cut(snapshot.modelKey, telemetryModelKeyLimit)
            );
            statement.setString(
                i++,
                cut(snapshot.modelName, telemetryModelNameLimit)
            );
            statement.setString(
                i++,
                snapshot.ownerId == null ? null : snapshot.ownerId.toString()
            );
            statement.setString(
                i++,
                cut(snapshot.ownerName, telemetryOwnerNameLimit)
            );
            statement.setLong(i++, Math.max(0L, snapshot.firstSeenAt));
            statement.setLong(i++, Math.max(0L, snapshot.spawnedAt));
            statement.setLong(i++, Math.max(0L, snapshot.lastUsedAt));
            statement.setLong(i++, Math.max(0L, snapshot.lastSeenAt));
            statement.setLong(i++, Math.max(0L, snapshot.lastEventAt));
            statement.setString(
                i++,
                cut(snapshot.lastEvent, telemetryEventTypeLimit)
            );
            statement.setString(
                i++,
                cut(snapshot.lastEventDetails, telemetryEventDetailsLimit)
            );
            statement.setString(i++, snapshot.worldName);
            statement.setDouble(i++, snapshot.x);
            statement.setDouble(i++, snapshot.y);
            statement.setDouble(i++, snapshot.z);
            statement.setFloat(i++, snapshot.yaw);
            statement.setFloat(i++, snapshot.pitch);
            statement.setDouble(i++, snapshot.health);
            statement.setDouble(i++, snapshot.healthMax);
            statement.setDouble(i++, snapshot.healthPercent);
            statement.setDouble(i++, snapshot.fuel);
            statement.setDouble(i++, snapshot.fuelMax);
            statement.setDouble(i++, snapshot.fuelPercent);
            statement.setBoolean(i++, snapshot.engineRunning);
            statement.setBoolean(i++, snapshot.headlightsOn);
            statement.setDouble(i++, snapshot.speedMps);
            statement.setInt(i++, snapshot.occupiedSeats);
            statement.setInt(i++, snapshot.totalSeats);
            statement.setDouble(i++, snapshot.damageFront);
            statement.setDouble(i++, snapshot.damageRear);
            statement.setDouble(i++, snapshot.damageWheelFl);
            statement.setDouble(i++, snapshot.damageWheelFr);
            statement.setDouble(i++, snapshot.damageWheelRl);
            statement.setDouble(i++, snapshot.damageWheelRr);
            statement.setString(
                i++,
                cut(snapshot.damageSnapshot, telemetryEventDetailsLimit)
            );
            statement.setLong(i++, now);
            statement.executeUpdate();
            return true;
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Failed to upsert telemetry state for " +
                        snapshot.vehicleId +
                        ": " +
                        ex.getMessage()
                );
            return false;
        }
    }

    public void appendTelemetryEvent(
        UUID vehicleId,
        String eventType,
        String eventDetails,
        Location location,
        long eventAtMs
    ) {
        if (
            !isEnabled() ||
            !telemetryEnabled ||
            !telemetryEventsEnabled ||
            vehicleId == null
        ) {
            return;
        }
        String sql =
            "INSERT INTO car_telemetry_events " +
            "(vehicle_id, event_type, event_details, event_at, created_at, world, x, y, z) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        long now = System.currentTimeMillis();
        String worldName =
            location != null && location.getWorld() != null
                ? location.getWorld().getName()
                : null;
        double x = location != null ? location.getX() : 0.0D;
        double y = location != null ? location.getY() : 0.0D;
        double z = location != null ? location.getZ() : 0.0D;

        try (
            Connection connection = openConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, vehicleId.toString());
            statement.setString(2, cut(eventType, telemetryEventTypeLimit));
            statement.setString(
                3,
                cut(eventDetails, telemetryEventDetailsLimit)
            );
            statement.setLong(4, Math.max(0L, eventAtMs));
            statement.setLong(5, now);
            statement.setString(6, worldName);
            statement.setDouble(7, x);
            statement.setDouble(8, y);
            statement.setDouble(9, z);
            statement.executeUpdate();
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Failed to append telemetry event for " +
                        vehicleId +
                        ": " +
                        ex.getMessage()
                );
        }
    }

    private String buildUpsertSql() {
        if (mode == Mode.MYSQL) {
            return (
                "INSERT INTO player_car_ownership " +
                "(vehicle_id, owner_uuid, owner_name, created_at, updated_at, world, x, y, z, yaw, health, fuel, engine_running, headlights_on) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "owner_uuid=VALUES(owner_uuid), " +
                "owner_name=VALUES(owner_name), " +
                "updated_at=VALUES(updated_at), " +
                "world=VALUES(world), " +
                "x=VALUES(x), " +
                "y=VALUES(y), " +
                "z=VALUES(z), " +
                "yaw=VALUES(yaw), " +
                "health=VALUES(health), " +
                "fuel=VALUES(fuel), " +
                "engine_running=VALUES(engine_running), " +
                "headlights_on=VALUES(headlights_on)"
            );
        }
        return (
            "INSERT INTO player_car_ownership " +
            "(vehicle_id, owner_uuid, owner_name, created_at, updated_at, world, x, y, z, yaw, health, fuel, engine_running, headlights_on) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT(vehicle_id) DO UPDATE SET " +
            "owner_uuid=excluded.owner_uuid, " +
            "owner_name=excluded.owner_name, " +
            "updated_at=excluded.updated_at, " +
            "world=excluded.world, " +
            "x=excluded.x, " +
            "y=excluded.y, " +
            "z=excluded.z, " +
            "yaw=excluded.yaw, " +
            "health=excluded.health, " +
            "fuel=excluded.fuel, " +
            "engine_running=excluded.engine_running, " +
            "headlights_on=excluded.headlights_on"
        );
    }

    public void deleteOwnership(UUID vehicleId) {
        if (!isEnabled() || vehicleId == null) {
            return;
        }
        String sql = "DELETE FROM player_car_ownership WHERE vehicle_id = ?";
        try (
            Connection connection = openConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, vehicleId.toString());
            statement.executeUpdate();
            if (telemetryEnabled) {
                try (
                    PreparedStatement deleteState = connection.prepareStatement(
                        "DELETE FROM car_telemetry_state WHERE vehicle_id = ?"
                    )
                ) {
                    deleteState.setString(1, vehicleId.toString());
                    deleteState.executeUpdate();
                }
            }
        } catch (Exception ex) {
            plugin
                .getLogger()
                .warning(
                    "Failed to delete ownership record for " +
                        vehicleId +
                        ": " +
                        ex.getMessage()
                );
        }
    }

    private void configureSqlite(FileConfiguration cfg)
        throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_SQLITE);
        String relativePath = cfg.getString(
            "database.sqlite.file",
            "player-cars.db"
        );
        File dbFile = new File(plugin.getDataFolder(), relativePath);
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        this.jdbcUrl = JDBC_SQLITE_PREFIX + dbFile.getAbsolutePath();
        this.username = null;
        this.password = null;
    }

    private void configureMySql(FileConfiguration cfg)
        throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_MYSQL);

        String host = cfg.getString(
            "database.mysql.host",
            "127.0.0.1"
        );
        int port = cfg.getInt(
            "database.mysql.port",
            3306
        );
        String schema = cfg.getString(
            "database.mysql.database",
            "coolcars"
        );
        String params = cfg.getString(
            "database.mysql.params",
            MYSQL_PARAMS_DEFAULT
        );
        this.jdbcUrl =
            JDBC_MYSQL_PREFIX +
            host +
            ":" +
            port +
            "/" +
            schema +
            "?" +
            params;
        this.username = cfg.getString(
            "database.mysql.username",
            "root"
        );
        this.password = cfg.getString(
            "database.mysql.password",
            ""
        );
    }

    private Connection openConnection() throws SQLException {
        DriverManager.setLoginTimeout(loginTimeoutSeconds);
        if (mode == Mode.MYSQL) {
            return DriverManager.getConnection(jdbcUrl, username, password);
        }
        return DriverManager.getConnection(jdbcUrl);
    }

    private void initSchema() throws SQLException {
        String createTable =
            "CREATE TABLE IF NOT EXISTS player_car_ownership (" +
            "vehicle_id VARCHAR(36) PRIMARY KEY," +
            "owner_uuid VARCHAR(36) NOT NULL," +
            "owner_name VARCHAR(32)," +
            "created_at BIGINT NOT NULL," +
            "updated_at BIGINT NOT NULL," +
            "world VARCHAR(64)," +
            "x DOUBLE," +
            "y DOUBLE," +
            "z DOUBLE," +
            "yaw FLOAT," +
            "health DOUBLE," +
            "fuel DOUBLE," +
            "engine_running BOOLEAN," +
            "headlights_on BOOLEAN" +
            ")";
        String createOwnerIndex = "CREATE INDEX IF NOT EXISTS idx_player_car_ownership_owner ON player_car_ownership(owner_uuid)";

        try (
            Connection connection = openConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(createTable);
            statement.executeUpdate(createOwnerIndex);
            if (telemetryEnabled) {
                statement.executeUpdate(buildCreateTelemetryStateSql());
                statement.executeUpdate(buildCreateTelemetryEventSql());
                executeSchemaSqlSafe(
                    connection,
                    mode == Mode.MYSQL
                        ? "CREATE INDEX idx_car_telemetry_state_owner ON car_telemetry_state(owner_id)"
                        : "CREATE INDEX IF NOT EXISTS idx_car_telemetry_state_owner ON car_telemetry_state(owner_id)"
                );
                executeSchemaSqlSafe(
                    connection,
                    mode == Mode.MYSQL
                        ? "CREATE INDEX idx_car_telemetry_state_last_used ON car_telemetry_state(last_used_at)"
                        : "CREATE INDEX IF NOT EXISTS idx_car_telemetry_state_last_used ON car_telemetry_state(last_used_at)"
                );
                executeSchemaSqlSafe(
                    connection,
                    mode == Mode.MYSQL
                        ? "CREATE INDEX idx_car_telemetry_events_vehicle_time ON car_telemetry_events(vehicle_id, event_at)"
                        : "CREATE INDEX IF NOT EXISTS idx_car_telemetry_events_vehicle_time ON car_telemetry_events(vehicle_id, event_at)"
                );
            }
        }
    }

    private void executeSchemaSqlSafe(Connection connection, String sql) {
        if (connection == null || sql == null || sql.isBlank()) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ignored) {}
    }

    private String buildCreateTelemetryStateSql() {
        return (
            "CREATE TABLE IF NOT EXISTS car_telemetry_state (" +
            "vehicle_id VARCHAR(36) PRIMARY KEY," +
            "model_key VARCHAR(96)," +
            "model_name VARCHAR(128)," +
            "owner_id VARCHAR(36)," +
            "owner_name VARCHAR(64)," +
            "first_seen_at BIGINT," +
            "spawned_at BIGINT," +
            "last_used_at BIGINT," +
            "last_seen_at BIGINT," +
            "last_event_at BIGINT," +
            "last_event VARCHAR(64)," +
            "last_event_details TEXT," +
            "world VARCHAR(64)," +
            "x DOUBLE," +
            "y DOUBLE," +
            "z DOUBLE," +
            "yaw FLOAT," +
            "pitch FLOAT," +
            "health DOUBLE," +
            "health_max DOUBLE," +
            "health_percent DOUBLE," +
            "fuel DOUBLE," +
            "fuel_max DOUBLE," +
            "fuel_percent DOUBLE," +
            "engine_running BOOLEAN," +
            "headlights_on BOOLEAN," +
            "speed_mps DOUBLE," +
            "occupied_seats INT," +
            "total_seats INT," +
            "damage_front DOUBLE," +
            "damage_rear DOUBLE," +
            "damage_wheel_fl DOUBLE," +
            "damage_wheel_fr DOUBLE," +
            "damage_wheel_rl DOUBLE," +
            "damage_wheel_rr DOUBLE," +
            "damage_snapshot TEXT," +
            "updated_at BIGINT" +
            ")"
        );
    }

    private String buildCreateTelemetryEventSql() {
        if (mode == Mode.MYSQL) {
            return (
                "CREATE TABLE IF NOT EXISTS car_telemetry_events (" +
                "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                "vehicle_id VARCHAR(36) NOT NULL," +
                "event_type VARCHAR(64) NOT NULL," +
                "event_details TEXT," +
                "event_at BIGINT NOT NULL," +
                "created_at BIGINT NOT NULL," +
                "world VARCHAR(64)," +
                "x DOUBLE," +
                "y DOUBLE," +
                "z DOUBLE" +
                ")"
            );
        }
        return (
            "CREATE TABLE IF NOT EXISTS car_telemetry_events (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "vehicle_id VARCHAR(36) NOT NULL," +
            "event_type VARCHAR(64) NOT NULL," +
            "event_details TEXT," +
            "event_at BIGINT NOT NULL," +
            "created_at BIGINT NOT NULL," +
            "world VARCHAR(64)," +
            "x DOUBLE," +
            "y DOUBLE," +
            "z DOUBLE" +
            ")"
        );
    }

    private String buildSqliteTelemetryUpsert() {
        return (
            "INSERT INTO car_telemetry_state (" +
            "vehicle_id, model_key, model_name, owner_id, owner_name, " +
            "first_seen_at, spawned_at, last_used_at, last_seen_at, " +
            "last_event_at, last_event, last_event_details, world, x, y, z, " +
            "yaw, pitch, health, health_max, health_percent, fuel, fuel_max, fuel_percent, " +
            "engine_running, headlights_on, speed_mps, occupied_seats, total_seats, " +
            "damage_front, damage_rear, damage_wheel_fl, damage_wheel_fr, damage_wheel_rl, damage_wheel_rr, " +
            "damage_snapshot, updated_at" +
            ") VALUES (" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
            ") ON CONFLICT(vehicle_id) DO UPDATE SET " +
            "model_key=excluded.model_key, model_name=excluded.model_name, " +
            "owner_id=excluded.owner_id, owner_name=excluded.owner_name, " +
            "first_seen_at=excluded.first_seen_at, spawned_at=excluded.spawned_at, " +
            "last_used_at=excluded.last_used_at, last_seen_at=excluded.last_seen_at, " +
            "last_event_at=excluded.last_event_at, last_event=excluded.last_event, " +
            "last_event_details=excluded.last_event_details, world=excluded.world, " +
            "x=excluded.x, y=excluded.y, z=excluded.z, yaw=excluded.yaw, pitch=excluded.pitch, " +
            "health=excluded.health, health_max=excluded.health_max, health_percent=excluded.health_percent, " +
            "fuel=excluded.fuel, fuel_max=excluded.fuel_max, fuel_percent=excluded.fuel_percent, " +
            "engine_running=excluded.engine_running, headlights_on=excluded.headlights_on, " +
            "speed_mps=excluded.speed_mps, occupied_seats=excluded.occupied_seats, total_seats=excluded.total_seats, " +
            "damage_front=excluded.damage_front, damage_rear=excluded.damage_rear, " +
            "damage_wheel_fl=excluded.damage_wheel_fl, damage_wheel_fr=excluded.damage_wheel_fr, " +
            "damage_wheel_rl=excluded.damage_wheel_rl, damage_wheel_rr=excluded.damage_wheel_rr, " +
            "damage_snapshot=excluded.damage_snapshot, updated_at=excluded.updated_at"
        );
    }

    private String buildMySqlTelemetryUpsert() {
        return (
            "INSERT INTO car_telemetry_state (" +
            "vehicle_id, model_key, model_name, owner_id, owner_name, " +
            "first_seen_at, spawned_at, last_used_at, last_seen_at, " +
            "last_event_at, last_event, last_event_details, world, x, y, z, " +
            "yaw, pitch, health, health_max, health_percent, fuel, fuel_max, fuel_percent, " +
            "engine_running, headlights_on, speed_mps, occupied_seats, total_seats, " +
            "damage_front, damage_rear, damage_wheel_fl, damage_wheel_fr, damage_wheel_rl, damage_wheel_rr, " +
            "damage_snapshot, updated_at" +
            ") VALUES (" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
            ") ON DUPLICATE KEY UPDATE " +
            "model_key=VALUES(model_key), model_name=VALUES(model_name), " +
            "owner_id=VALUES(owner_id), owner_name=VALUES(owner_name), " +
            "first_seen_at=VALUES(first_seen_at), spawned_at=VALUES(spawned_at), " +
            "last_used_at=VALUES(last_used_at), last_seen_at=VALUES(last_seen_at), " +
            "last_event_at=VALUES(last_event_at), last_event=VALUES(last_event), " +
            "last_event_details=VALUES(last_event_details), world=VALUES(world), " +
            "x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch), " +
            "health=VALUES(health), health_max=VALUES(health_max), health_percent=VALUES(health_percent), " +
            "fuel=VALUES(fuel), fuel_max=VALUES(fuel_max), fuel_percent=VALUES(fuel_percent), " +
            "engine_running=VALUES(engine_running), headlights_on=VALUES(headlights_on), " +
            "speed_mps=VALUES(speed_mps), occupied_seats=VALUES(occupied_seats), total_seats=VALUES(total_seats), " +
            "damage_front=VALUES(damage_front), damage_rear=VALUES(damage_rear), " +
            "damage_wheel_fl=VALUES(damage_wheel_fl), damage_wheel_fr=VALUES(damage_wheel_fr), " +
            "damage_wheel_rl=VALUES(damage_wheel_rl), damage_wheel_rr=VALUES(damage_wheel_rr), " +
            "damage_snapshot=VALUES(damage_snapshot), updated_at=VALUES(updated_at)"
        );
    }

    private static String cut(String value, int maxLen) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLen) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(0, maxLen));
    }

    private static UUID parseUuid(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
