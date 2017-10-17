package cn.rongcapital.chorus.authorization.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * DW
 * Created by shicheng on 2017/3/8.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationDataDW extends AuthorizationData {

    private List databases; // 数据库
    private List tables; // 数据表
    private List columns; // 数据列

}
