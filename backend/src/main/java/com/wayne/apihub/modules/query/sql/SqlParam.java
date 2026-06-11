package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.model.BaseDataObject;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SqlParam extends BaseDataObject {
    private Long apiId;
    private String name;
    private String type;
    private String description;
}
