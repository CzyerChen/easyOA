package easyoa.rulemanager.config.cache;

import com.alibaba.fastjson.parser.ParserConfig;
import easyoa.rulemanager.serializer.FastJsonRedisSerializer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;

/**
 * Created by claire on 2019-06-21 - 17:27
 **/
@Configuration
public class RedissonConfig extends CachingConfigurerSupport {

    @Value("${redisson.config.file}")
    private String redissonFile;

    @Bean(name = "redissonClient")
    public RedissonClient redissonClient() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(redissonFile);
        Config config = Config.fromYAML(classPathResource.getInputStream());
        return Redisson.create(config);
    }

    @Bean(name = "cacheManager")
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //自定义的fastjson序列化方式
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer =  new FastJsonRedisSerializer<>(Object.class);
        //封装成pair
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
        //获取默认配置
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        //封装默认值，返回新对象
        RedisCacheConfiguration redisCacheConfiguration =  configuration.entryTtl(Duration.ofSeconds(100));
        //组装cachemanager
        RedisCacheManager redisCacheManager =  new RedisCacheManager(redisCacheWriter,redisCacheConfiguration);
        ParserConfig.getGlobalInstance().addAccept("easyoa.common.domain");
        ParserConfig.getGlobalInstance().addAccept("easyoa.core.domain");
        ParserConfig.getGlobalInstance().addAccept("easyoa.leavemanager.domain");
        return redisCacheManager;
    }

    @Bean(name = "redisCacheConfiguration")
    public RedisCacheConfiguration redisCacheConfiguration(){
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer)).entryTtl(Duration.ofDays(7));
        return configuration;
    }


    @Bean(name = "redisTemplate")
    @SuppressWarnings("unchecked")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory  redisConnectionFactory){
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);

        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return  template;
    }


}
