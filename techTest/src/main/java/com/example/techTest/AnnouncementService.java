package com.example.techTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    public Announcement getAnnouncementById(Long id) {
        Optional<Announcement> optional = announcementRepository.findById(id);
        return optional.orElse(null);
    }

    public void updateAnnouncement(Long id, Announcement updatedAnnouncement) {
        Optional<Announcement> optional = announcementRepository.findById(id);
        if (optional.isPresent()) {
            Announcement existingAnnouncement = optional.get();
            existingAnnouncement.setTitle(updatedAnnouncement.getTitle());
            existingAnnouncement.setContent(updatedAnnouncement.getContent());
            existingAnnouncement.setPublishDate(updatedAnnouncement.getPublishDate());
            if (updatedAnnouncement.getFileData() != null) {
                existingAnnouncement.setFileName(updatedAnnouncement.getFileName());
                existingAnnouncement.setFileData(updatedAnnouncement.getFileData());
            }
            announcementRepository.save(existingAnnouncement);
        }
    }



}
