package com.zeng.business.mapper;

import com.zeng.business.entity.TestRecord;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface TestRecordMapper extends Mapper<TestRecord> {
}
