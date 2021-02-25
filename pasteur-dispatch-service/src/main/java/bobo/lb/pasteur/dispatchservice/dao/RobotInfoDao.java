package bobo.lb.pasteur.dispatchservice.dao;

import bobo.lb.pasteur.dispatchservice.dao.entity.RobotInfoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RobotInfoDao {

    long save(RobotInfoEntity robotInfo);

    RobotInfoEntity queryById(long id);

    RobotInfoEntity update(long id, RobotInfoEntity robotInfo);

}
