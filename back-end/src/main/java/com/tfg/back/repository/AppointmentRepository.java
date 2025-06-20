package com.tfg.back.repository;

import com.tfg.back.model.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateTime(UUID doctorId, LocalDateTime appointmentDateTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND DATE(a.appointmentDateTime) = :date")
    List<Appointment> findByDoctorIdAndAppointmentDate(@Param("doctorId") UUID doctorId,
                                                       @Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT a.client.id) FROM Appointment a WHERE a.doctor.id = :doctorId")
    Long countDistinctClientsByDoctorId(@Param("doctorId") UUID doctorId);

    List<Appointment> findByClientId(UUID id);

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.client.id = :email
            AND a.status <> 'CANCELLED'
        ORDER BY a.appointmentDateTime DESC
        """)
    List<Appointment> findAppointmentsByClientEmail(@Param("email") UUID email, Pageable pageable);
}
