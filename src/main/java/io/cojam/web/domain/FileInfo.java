package io.cojam.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class FileInfo {
    private String fileKey;
    private String tableKey;
    private String fileRealName;
    private String fileChangeName;
    private String filePath;
    private String fileSize;
    private String fileExtension;
    @JsonIgnore
    private String createMemberKey;
    @JsonIgnore
    private String createDateTime;
    @JsonIgnore
    private String updateMemberKey;
    @JsonIgnore
    private String updateDateTime;
    private Boolean deleted;

}
