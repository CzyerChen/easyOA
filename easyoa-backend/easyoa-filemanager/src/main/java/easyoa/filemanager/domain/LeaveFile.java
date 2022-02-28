package easyoa.filemanager.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by claire on 2019-07-11 - 10:36
 **/
@Entity
@Table(name = "fb_leave_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private Long userId;
    private String fileLocalPath;
    private String fileRemotePath;
    private String fileContent;
    private String fileOriginName;
    private String fileCurrentName;

}
