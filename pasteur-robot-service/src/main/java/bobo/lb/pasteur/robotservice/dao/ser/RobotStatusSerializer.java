package bobo.lb.pasteur.robotservice.dao.ser;

import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class RobotStatusSerializer implements RedisSerializer<RobotStatus> {

    private RuntimeSchema<RobotStatus> schema = RuntimeSchema.createFrom(RobotStatus.class);

    @Override
    public byte[] serialize(RobotStatus status) throws SerializationException {
        return ProtostuffIOUtil.toByteArray(status, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    @Override
    public RobotStatus deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null) {
            return null;
        }
        RobotStatus status = new RobotStatus();
        ProtostuffIOUtil.mergeFrom(bytes, status, schema);
        return status;
    }
}
