package com.jigong.app_attendance.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jigong.app_attendance.bean.AttendanceInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ATTENDANCE_INFO".
*/
public class AttendanceInfoDao extends AbstractDao<AttendanceInfo, Long> {

    public static final String TABLENAME = "ATTENDANCE_INFO";

    /**
     * Properties of entity AttendanceInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AttendanceId = new Property(1, String.class, "attendanceId", false, "ATTENDANCE_ID");
        public final static Property CheckinTime = new Property(2, String.class, "checkinTime", false, "CHECKIN_TIME");
        public final static Property DeviceSerialNo = new Property(3, String.class, "deviceSerialNo", false, "DEVICE_SERIAL_NO");
        public final static Property MachineType = new Property(4, String.class, "machineType", false, "MACHINE_TYPE");
        public final static Property NormalSignImage = new Property(5, String.class, "normalSignImage", false, "NORMAL_SIGN_IMAGE");
        public final static Property ProjectId = new Property(6, String.class, "projectId", false, "PROJECT_ID");
        public final static Property RedSignImage = new Property(7, String.class, "redSignImage", false, "RED_SIGN_IMAGE");
        public final static Property SubcontractorId = new Property(8, String.class, "subcontractorId", false, "SUBCONTRACTOR_ID");
        public final static Property Temperature = new Property(9, String.class, "temperature", false, "TEMPERATURE");
        public final static Property WorkerId = new Property(10, String.class, "workerId", false, "WORKER_ID");
        public final static Property WorkerName = new Property(11, String.class, "workerName", false, "WORKER_NAME");
        public final static Property IdNumber = new Property(12, String.class, "idNumber", false, "ID_NUMBER");
    }


    public AttendanceInfoDao(DaoConfig config) {
        super(config);
    }
    
    public AttendanceInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ATTENDANCE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ATTENDANCE_ID\" TEXT," + // 1: attendanceId
                "\"CHECKIN_TIME\" TEXT," + // 2: checkinTime
                "\"DEVICE_SERIAL_NO\" TEXT," + // 3: deviceSerialNo
                "\"MACHINE_TYPE\" TEXT," + // 4: machineType
                "\"NORMAL_SIGN_IMAGE\" TEXT," + // 5: normalSignImage
                "\"PROJECT_ID\" TEXT," + // 6: projectId
                "\"RED_SIGN_IMAGE\" TEXT," + // 7: redSignImage
                "\"SUBCONTRACTOR_ID\" TEXT," + // 8: subcontractorId
                "\"TEMPERATURE\" TEXT," + // 9: temperature
                "\"WORKER_ID\" TEXT," + // 10: workerId
                "\"WORKER_NAME\" TEXT," + // 11: workerName
                "\"ID_NUMBER\" TEXT);"); // 12: idNumber
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ATTENDANCE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AttendanceInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String attendanceId = entity.getAttendanceId();
        if (attendanceId != null) {
            stmt.bindString(2, attendanceId);
        }
 
        String checkinTime = entity.getCheckinTime();
        if (checkinTime != null) {
            stmt.bindString(3, checkinTime);
        }
 
        String deviceSerialNo = entity.getDeviceSerialNo();
        if (deviceSerialNo != null) {
            stmt.bindString(4, deviceSerialNo);
        }
 
        String machineType = entity.getMachineType();
        if (machineType != null) {
            stmt.bindString(5, machineType);
        }
 
        String normalSignImage = entity.getNormalSignImage();
        if (normalSignImage != null) {
            stmt.bindString(6, normalSignImage);
        }
 
        String projectId = entity.getProjectId();
        if (projectId != null) {
            stmt.bindString(7, projectId);
        }
 
        String redSignImage = entity.getRedSignImage();
        if (redSignImage != null) {
            stmt.bindString(8, redSignImage);
        }
 
        String subcontractorId = entity.getSubcontractorId();
        if (subcontractorId != null) {
            stmt.bindString(9, subcontractorId);
        }
 
        String temperature = entity.getTemperature();
        if (temperature != null) {
            stmt.bindString(10, temperature);
        }
 
        String workerId = entity.getWorkerId();
        if (workerId != null) {
            stmt.bindString(11, workerId);
        }
 
        String workerName = entity.getWorkerName();
        if (workerName != null) {
            stmt.bindString(12, workerName);
        }
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(13, idNumber);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AttendanceInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String attendanceId = entity.getAttendanceId();
        if (attendanceId != null) {
            stmt.bindString(2, attendanceId);
        }
 
        String checkinTime = entity.getCheckinTime();
        if (checkinTime != null) {
            stmt.bindString(3, checkinTime);
        }
 
        String deviceSerialNo = entity.getDeviceSerialNo();
        if (deviceSerialNo != null) {
            stmt.bindString(4, deviceSerialNo);
        }
 
        String machineType = entity.getMachineType();
        if (machineType != null) {
            stmt.bindString(5, machineType);
        }
 
        String normalSignImage = entity.getNormalSignImage();
        if (normalSignImage != null) {
            stmt.bindString(6, normalSignImage);
        }
 
        String projectId = entity.getProjectId();
        if (projectId != null) {
            stmt.bindString(7, projectId);
        }
 
        String redSignImage = entity.getRedSignImage();
        if (redSignImage != null) {
            stmt.bindString(8, redSignImage);
        }
 
        String subcontractorId = entity.getSubcontractorId();
        if (subcontractorId != null) {
            stmt.bindString(9, subcontractorId);
        }
 
        String temperature = entity.getTemperature();
        if (temperature != null) {
            stmt.bindString(10, temperature);
        }
 
        String workerId = entity.getWorkerId();
        if (workerId != null) {
            stmt.bindString(11, workerId);
        }
 
        String workerName = entity.getWorkerName();
        if (workerName != null) {
            stmt.bindString(12, workerName);
        }
 
        String idNumber = entity.getIdNumber();
        if (idNumber != null) {
            stmt.bindString(13, idNumber);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AttendanceInfo readEntity(Cursor cursor, int offset) {
        AttendanceInfo entity = new AttendanceInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // attendanceId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // checkinTime
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceSerialNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // machineType
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // normalSignImage
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // projectId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // redSignImage
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // subcontractorId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // temperature
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // workerId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // workerName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // idNumber
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AttendanceInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAttendanceId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCheckinTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceSerialNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMachineType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNormalSignImage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setProjectId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRedSignImage(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSubcontractorId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTemperature(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setWorkerId(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setWorkerName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIdNumber(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AttendanceInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AttendanceInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AttendanceInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
