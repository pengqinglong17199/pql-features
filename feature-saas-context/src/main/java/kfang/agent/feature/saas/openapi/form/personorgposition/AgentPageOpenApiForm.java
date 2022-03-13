package kfang.agent.feature.saas.openapi.form.personorgposition;

import io.swagger.annotations.ApiModelProperty;
import kfang.infra.common.model.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * openApi对外开放form
 *
 * @author pengqinglong
 * @since 2021/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentPageOpenApiForm extends AgentOpenApiForm {

    @ApiModelProperty(value = "keyword关键字")
    private String keyword;

    @ApiModelProperty(value = "页容量参数，默认20", example = "20")
    @Min(message = "页容量参数不得为空", value = 1)
    private int pageSize = 20;

    @ApiModelProperty(value = "当前页，默认1", example = "1")
    @Min(message = "当前页参数不得为空", value = 1)
    private int currentPage = 1;

    @ApiModelProperty(value = "是否查询总数", hidden = true)
    private boolean queryRecordCount = true;

    public <T> Pagination<T> makePagination() {
        return new Pagination<>(pageSize, currentPage, queryRecordCount);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isQueryRecordCount() {
        return queryRecordCount;
    }

    public void setQueryRecordCount(boolean queryRecordCount) {
        this.queryRecordCount = queryRecordCount;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


}