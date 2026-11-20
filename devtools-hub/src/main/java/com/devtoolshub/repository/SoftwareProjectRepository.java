
package com.devtoolshub.repository;

import com.devtoolshub.entity.SoftwareProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoftwareProjectRepository
        extends JpaRepository<SoftwareProject, Long> {
}
