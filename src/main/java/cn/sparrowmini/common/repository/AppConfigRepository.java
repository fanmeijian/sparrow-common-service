package cn.sparrowmini.common.repository;

import cn.sparrowmini.common.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
}
