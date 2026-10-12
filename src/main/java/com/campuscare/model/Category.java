package com.campuscare.model;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private Integer defaultDepartmentId;
    private String defaultDepartmentName;
    private boolean active;

    public Category() {}

    public Category(int categoryId, String categoryName, String description,
                    Integer defaultDepartmentId, boolean active) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.defaultDepartmentId = defaultDepartmentId;
        this.active = active;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDefaultDepartmentId() { return defaultDepartmentId; }
    public void setDefaultDepartmentId(Integer defaultDepartmentId) { this.defaultDepartmentId = defaultDepartmentId; }

    public String getDefaultDepartmentName() { return defaultDepartmentName; }
    public void setDefaultDepartmentName(String defaultDepartmentName) { this.defaultDepartmentName = defaultDepartmentName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
