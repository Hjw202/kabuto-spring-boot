package com.kabuto.cloud.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 *
 * <p><b>需求描述：</b>大王要求封装统一响应类，统一前后端交互数据结构</p>
 * <p><b>解决方案：</b>提取通用的分页结果封装类，所有 Controller 可复用</p>
 * <p><b>原因说明：</b>参考 nest-admin R.ts 的 TableData 方法设计，Spring Boot 项目通用做法。
 * 从 DemoController 中提取为独立类，避免每个 Controller 重复定义</p>
 *
 * @param <T> 数据项类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果封装体")
public class PageResult<T> {

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer page;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize;
}
