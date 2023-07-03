package com.tianji.learning.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.dto.QuestionFormUpdateDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.query .QuestionAdminPageQuery;
import com.tianji.learning.domain.query.QuestionPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;

/**
 * <p>
 * 互动提问的问题表 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-19
 */
public interface IInteractionQuestionService extends IService<InteractionQuestion> {

    void saveQuestion(QuestionFormDTO questionForm);

    void updateQuestion(Long id, QuestionFormUpdateDTO questionForm);

    void deleteQuestion(Long id);

    PageDTO<QuestionVO> pageQueryQuestion(QuestionPageQuery query);

    QuestionVO queryQuestionById(Long id);

    PageDTO<QuestionAdminVO> adminPageQueryQuestion(QuestionAdminPageQuery query);

    QuestionAdminVO adminQueryQuestionById(Long id);

    void hiddenQuestion(Long id, Boolean hidden);
}
