package bobo.lb.pasteur.robotclient;

import bobo.lb.pasteur.robotservice.dto.RobotInfo;
import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class Serializer {

    private RuntimeSchema<RobotStatus> statusRuntimeSchema = RuntimeSchema.createFrom(RobotStatus.class);
    private RuntimeSchema<RobotInfo> infoRuntimeSchema = RuntimeSchema.createFrom(RobotInfo.class);

    public byte[] serializeInfo(RobotInfo info) {
        return ProtostuffIOUtil.toByteArray(info, infoRuntimeSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public byte[] serializeStatus(RobotStatus status) {
        return ProtostuffIOUtil.toByteArray(status, statusRuntimeSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }
}
