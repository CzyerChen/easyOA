package easyoa.leavemanager.service.impl;

import easyoa.leavemanager.domain.user.UserReporter;
import easyoa.leavemanager.service.UserReporterService;
import easyoa.core.domain.dto.ReportRelationshipExcel;
import easyoa.core.domain.po.user.User;
import easyoa.core.service.UserService;
import easyoa.leavemanager.repository.user.UserReporterRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by claire on 2019-07-26 - 13:35
 **/
@Service("userReporterService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserReporterServiceImpl implements UserReporterService {
    @Autowired
    private UserReporterRepository userReporterRepository;
    @Autowired
    private UserService userService;

    @Override
    public void saveUserReporterInfo(List<ReportRelationshipExcel> list) {
        List<UserReporter> reporters = userReporterFromExcel(list);
        if (reporters != null && reporters.size() != 0) {
          reporters.forEach(r ->{
              UserReporter code = userReporterRepository.findByUserCodeAndReporterCode(r.getUserCode(), r.getReporterCode());
              if(code == null){
                  userReporterRepository.save(r);
              }
          });
        }
    }

    @Override
    public User findUserAssignee(Long userId, List<String> users) {
        if (users != null && users.size() != 0) {
            List<User> assignees = userService.getUserByIds(users.stream().map(Long::valueOf).collect(Collectors.toList()));
            if(assignees != null && assignees.size() != 0) {
                List<String> strings = assignees.stream().map(User::getUserCode).distinct().collect(Collectors.toList());
                User user = userService.getUser(userId);
                if (user != null) {
                    UserReporter reporter = userReporterRepository.findFirstByUserCodeAndReporterCodeIn(user.getUserCode(),strings);
                    if (reporter != null) {
                        String reporterCode = reporter.getReporterCode();
                        return userService.getUserByCode(reporterCode);
                    }
                }
            }
        }
        return null;
    }

    private List<UserReporter> userReporterFromExcel(List<ReportRelationshipExcel> list) {
        List<UserReporter> list1 = new ArrayList<>();
        if (list != null && list.size() != 0) {
            list.forEach(l -> {
                if (StringUtils.isNotBlank(l.getUserCode()) && StringUtils.isNotBlank(l.getUserName())) {
                    UserReporter reporter = new UserReporter();
                    reporter.setUserCode(l.getUserCode());
                    reporter.setUserName(l.getUserName());
                    if (StringUtils.isNotBlank(l.getReporterCode()) && StringUtils.isNotBlank(l.getReporterName())) {

                        reporter.setReporterCode(l.getReporterCode());
                        reporter.setReporterName(l.getReporterName());
                        list1.add(reporter);
                    }

                    UserReporter otherReporter = new UserReporter();
                    otherReporter.setUserCode(l.getUserCode());
                    otherReporter.setUserName(l.getUserName());
                    if (StringUtils.isNotBlank(l.getOtherReporterCode()) && StringUtils.isNotBlank(l.getOtherReporterName())) {
                        otherReporter.setReporterCode(l.getOtherReporterCode());
                        otherReporter.setReporterName(l.getOtherReporterName());
                        list1.add(otherReporter);
                    }
                }
            });
        }
        return list1;
    }
}
