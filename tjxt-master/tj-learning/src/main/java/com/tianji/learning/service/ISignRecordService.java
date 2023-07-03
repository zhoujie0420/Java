package com.tianji.learning.service;

import com.tianji.learning.domain.vo.SignResultVO;

/**
 * @Description:
 * @Date: 2023/4/21 16:43
 */
public interface ISignRecordService {
    SignResultVO addSignRecords();

    Byte[] querySignRecords();
}
