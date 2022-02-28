package easyoa.core.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import org.hibernate.id.IncrementGenerator;

import java.io.Serializable;

/**
 * 自定义主键递增策略，避免手动设置的id被jpa自增id值覆盖的问题，jpa自增id存在无法回溯，可能长期积累无法使用
 * Created by claire on 2019-06-20 - 19:44
 **/
public class UserIdentityGenerator extends IncrementGenerator {

    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(null, object).getIdentifier(object, session);
        return id != null ? id : super.generate(session, object);
    }


}
