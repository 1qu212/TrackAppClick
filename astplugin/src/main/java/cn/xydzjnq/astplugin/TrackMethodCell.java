package cn.xydzjnq.astplugin;

import java.util.ArrayList;
import java.util.List;

public class TrackMethodCell {
    /**
     * 方法名
     */
    private String name;
    /**
     * 方法返回值
     */
    private String returnType;
    /**
     * 方法参数列表
     */
    private List<String> paramsType;
    /**
     * 埋点代码插入位置
     */
    private InsertLocation insertLocation;

    public TrackMethodCell(String name, String returnType, List<String> paramsType, InsertLocation insertLocation) {
        this.name = name;
        this.returnType = returnType;
        this.paramsType = paramsType;
        this.insertLocation = insertLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getParamsType() {
        if (paramsType == null) {
            return new ArrayList<>();
        }
        return paramsType;
    }

    public void setParamsType(List<String> paramsType) {
        this.paramsType = paramsType;
    }

    public InsertLocation getInsertLocation() {
        return insertLocation;
    }

    public void setInsertLocation(InsertLocation insertLocation) {
        this.insertLocation = insertLocation;
    }
}
