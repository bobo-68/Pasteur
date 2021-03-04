package bobo.lb.pasteur.robotservice.socket;

import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.Arrays;

public class Serializer {

    private RuntimeSchema<RobotStatus> statusRuntimeSchema = RuntimeSchema.createFrom(RobotStatus.class);
    private RuntimeSchema<RobotInfo> infoRuntimeSchema = RuntimeSchema.createFrom(RobotInfo.class);

    public byte[] serializeInfo(RobotInfo info) {
        return ProtostuffIOUtil.toByteArray(info, infoRuntimeSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public byte[] serializeStatus(RobotStatus status) {
        return ProtostuffIOUtil.toByteArray(status, statusRuntimeSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public Object deserialize(byte[] bytes) {
        byte[] realBytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        if(bytes[0] == 0) {
            RobotInfo res = new RobotInfo();
            ProtostuffIOUtil.mergeFrom(realBytes, res, infoRuntimeSchema);
            return res;
        } else if(bytes[0] == 1) {
            RobotStatus res = new RobotStatus();
            ProtostuffIOUtil.mergeFrom(realBytes, res, statusRuntimeSchema);
            return res;
        }
        return null;
    }
}
