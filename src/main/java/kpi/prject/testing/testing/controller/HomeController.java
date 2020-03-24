package kpi.prject.testing.testing.controller;

import kpi.prject.testing.testing.dto.DeclineReasonDTO;
import kpi.prject.testing.testing.dto.ReportDTO;
import kpi.prject.testing.testing.entity.Report;
import kpi.prject.testing.testing.entity.User;
import kpi.prject.testing.testing.entity.enums.ReportStatus;
import kpi.prject.testing.testing.entity.enums.Role;
import kpi.prject.testing.testing.repository.UserRepository;
import kpi.prject.testing.testing.service.ReportService;
import kpi.prject.testing.testing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final UserService userService;
    private final ReportService reportService;

    public HomeController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @GetMapping("")
    public String getHome(Model model, Principal principal, @PageableDefault(sort = {"updated", "id"}, direction = Sort.Direction.DESC, size = 12) Pageable pageable) {
        if (userService.getByUsername(principal.getName()).isPresent()){
            User user = userService.getByUsername(principal.getName()).get();
            if(user.getRole() == Role.ROLE_USER){
                model.addAttribute("username", principal.getName());
                model.addAttribute("reports", reportService.getAllByUser(user, pageable));
                return "home/userHome";
            } else if (user.getRole() == Role.ROLE_INSPECTOR){
                model.addAttribute("username", principal.getName());
                model.addAttribute("reports", reportService.getAllByInspectorAndStatus(user, ReportStatus.QUEUE, pageable));
                return "home/inspHome";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/add")
    public String getAdd(@ModelAttribute("report") ReportDTO report, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "home/addReport";
    }

    @PostMapping("/add")
    public String postAdd(@ModelAttribute("report") ReportDTO report, Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        User user = userService.getByUsername(principal.getName()).orElse(new User());
        reportService.save(reportService.getFromDTO(report), user);
        return "redirect:/home";
    }

    @PostMapping(value = "/accept/{report_id}")
    public String acceptReport(@PathVariable String report_id) {
        Optional<Report> reportToAccpet = reportService.getById(Long.parseLong(report_id));
        if(!reportToAccpet.isPresent()){
            return "redirect:/home";
        }
        reportService.acceptReport(reportToAccpet.get());
        return "redirect:/home";
    }

    @GetMapping(value = "/decline/{report_id}")
    public String declineReport(@ModelAttribute("report_reason") DeclineReasonDTO reportReason, @PathVariable String report_id,
                                Model model,
                                Principal principal) {
        Optional<Report> reportToDecline = reportService.getById(Long.parseLong(report_id));
        if(!reportToDecline.isPresent()){
            return "redirect:/home";
        }
        model.addAttribute("username", principal.getName());
        model.addAttribute("report_id", report_id);
        return "home/addReason";
    }

    @PostMapping(value = "/decline/{report_id}")
    public String declineReportPost(@ModelAttribute("report_reason") DeclineReasonDTO reportReason, @PathVariable String report_id,
                                Model model,
                                Principal principal) {
        Optional<Report> reportToDecline = reportService.getById(Long.parseLong(report_id));
        if(!reportToDecline.isPresent()){
            return "redirect:/home";
        }
        model.addAttribute("username", principal.getName());
        reportService.declineReport(reportToDecline.get(), reportReason);
        return "redirect:/home";
    }
}

