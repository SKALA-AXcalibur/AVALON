package com.sk.skala.axcalibur.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "parameter")
public class Parameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer parameterKey;    // 파라미터 키 (PK, AUTO_INCREMENT)
    
    @Column(name = "id", unique = true, nullable = false, length = 30)
    private String parameterId;      // 파라미터 ID (UNIQUE)

    @Column(name = "category_key")
    private Integer categoryKey;      // 카테고리 키 (FK)

    @Column(name = "name_ko", length = 100)
    private String nameKo;           // 파라미터 한글명

    @Column(name = "name", length = 100)
    private String name;           // 파라미터 영문명

    @Column(name = "context_key")
    private Integer contextKey;       // 컨텍스트 키 (FK)

    @Column(name = "data_type", length = 20)
    private String dataType;         // 데이터 타입

    @Column(name = "length")
    private Integer length;           // 길이

    @Column(name = "format", length = 30)
    private String format;           // 형식

    @Column(name = "default_value", length = 255)
    private String defaultValue;      // 기본값

    @Column(name = "required")
    private Boolean required;         // 필수 여부

    @Column(name = "parent_key")
    private Integer parentKey;        // 부모 키 (FK)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;      // 설명

    @Column(name = "apilist_key")
    private Integer apiListKey;      // API 목록 키 (FK)
    
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apilist_key", insertable = false, updatable = false)
    private ApiList apiList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_key", insertable = false, updatable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_key", insertable = false, updatable = false)
    private Context context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_key", insertable = false, updatable = false)
    private Parameter parent;

    @OneToMany(mappedBy = "parameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestcaseData> testcaseData = new ArrayList<>();    // 테스트케이스데이터 목록 (1:N)

    public Parameter() {}

    // getter/setter
    public Integer getParameterKey() {
        return parameterKey;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {    
        this.parameterId = parameterId;
    }

    public Integer getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(Integer categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getNameKo() {
        return nameKo;
    }   

    public void setNameKo(String nameKo) {
        this.nameKo = nameKo;
    }   

    public String getName() {
        return name;
    }   

    public void setName(String name) {
        this.name = name;
    }   

    public Integer getContextKey() {
        return contextKey;
    }

    public void setContextKey(Integer contextKey) {
        this.contextKey = contextKey;
    }

    public String getDataType() {
        return dataType;
    }   

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }           

    public Integer getLength() {
        return length;
    }   

    public void setLength(Integer length) { 
        this.length = length;
    }   

    public String getFormat() {
        return format;
    }      

    public void setFormat(String format) {
        this.format = format;
    }          

    public String getDefaultValue() {
        return defaultValue;
    }   

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }   

    public Boolean getRequired() {
        return required;
    }   

    public void setRequired(Boolean required) {
        this.required = required;
    }   

    public Integer getParentKey() {
        return parentKey;
    }

    public void setParentKey(Integer parentKey) {
        this.parentKey = parentKey;
    }

    public Integer getApiListKey() {
        return apiListKey;
    }

    public void setApiListKey(Integer apiListKey) {
        this.apiListKey = apiListKey;
    }

    public ApiList getApiList() {
        return apiList;
    }   

    public void setApiList(ApiList apiList) {
        this.apiList = apiList;
        if (apiList != null) {
            this.apiListKey = apiList.getApiListKey();
        }
    }

    public Category getCategory() {
        return category;
    }   

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryKey = category.getCategoryKey();
        }
    }

    public Context getContext() {
        return context;
    }   

    public void setContext(Context context) {
        this.context = context;
        if (context != null) {
            this.contextKey = context.getContextKey();
        }
    }

    public Parameter getParent() {
        return parent;
    }          

    public void setParent(Parameter parent) {
        this.parent = parent;
        if (parent != null) {
            this.parentKey = parent.getParameterKey();
        }
    }

    public List<TestcaseData> getTestcaseData() {
        return testcaseData;
    }   

    public String getDescription() {
        return description;
    }   

    public void setDescription(String description) {
        this.description = description;
    }   
}