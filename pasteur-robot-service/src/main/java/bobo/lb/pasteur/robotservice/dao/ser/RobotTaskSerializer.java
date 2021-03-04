package bobo.lb.pasteur.robotservice.dao.ser;

import bobo.lb.pasteur.robotservice.dto.RobotStatus;
import bobo.lb.pasteur.robotservice.dto.RobotTask;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class RobotTaskSerializer implements RedisSerializer<RobotTask> {

    private RuntimeSchema<RobotTask> schema = RuntimeSchema.createFrom(RobotTask.class);

    @Override
    public byte[] serialize(RobotTask robotTask) throws SerializationException {
        return ProtostuffIOUtil.toByteArray(robotTask, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    @Override
    public RobotTask deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null) {
            return null;
        }
        RobotTask task = new RobotTask();
        ProtostuffIOUtil.mergeFrom(bytes, task, schema);
        return task;
    }
}
