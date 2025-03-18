package com.example.techTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public String getAll(Model model) {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);
        return "announcements"; // 回傳 Thymeleaf 頁面
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "add";
    }

    @PostMapping("/add")
    public String addAnnouncement(@ModelAttribute Announcement announcement,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            announcement.setFileName(file.getOriginalFilename());
            announcement.setFileData(file.getBytes());
        }
        announcement.setPublishDate(LocalDate.now()); // 設定發布日期為當天
        announcementService.saveAnnouncement(announcement);
        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/announcements";
    }
}
