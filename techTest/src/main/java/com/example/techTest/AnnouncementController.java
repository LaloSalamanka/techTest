package com.example.techTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        return "announcements"; // Thymeleaf 頁面
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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        System.out.println(announcement);
        if (announcement == null) {
            return "redirect:/announcements"; // 若 ID 無效，回到列表頁
        }
        model.addAttribute("announcement", announcement);
        System.out.println(announcement.getPublishDate());
        return "edit"; // 這是編輯用的 Thymeleaf 頁面
    }

    @PostMapping("/edit/{id}")
    public String editAnnouncement(@PathVariable Long id,
                                   @ModelAttribute Announcement announcement,
                                   @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Announcement existingAnnouncement = announcementService.getAnnouncementById(id);
        if (existingAnnouncement == null) {
            return "redirect:/announcements"; // 若 ID 無效，回到列表頁
        }

        existingAnnouncement.setTitle(announcement.getTitle());
        existingAnnouncement.setContent(announcement.getContent().replaceAll("^<p>(.*?)</p>$", "$1"));
        existingAnnouncement.setPublishDate(announcement.getPublishDate());
        existingAnnouncement.setExpireDate(announcement.getExpireDate());
        existingAnnouncement.setAuthor(announcement.getAuthor());

        if (!file.isEmpty()) {
            existingAnnouncement.setFileName(file.getOriginalFilename());
            existingAnnouncement.setFileData(file.getBytes());
        }

        announcementService.saveAnnouncement(existingAnnouncement);
        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/announcements";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);

        if (announcement == null || announcement.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + announcement.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(announcement.getFileData());
    }

}
