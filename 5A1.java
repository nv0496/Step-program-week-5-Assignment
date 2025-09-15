import java.time.LocalDate;
import java.util.*;

// ===============================
// a. Immutable MedicalRecord
// ===============================
final class MedicalRecord {
    private final String recordId;
    private final String patientDNA;
    private final String[] allergies;
    private final String[] medicalHistory;
    private final LocalDate birthDate;
    private final String bloodType;

    public MedicalRecord(String recordId, String patientDNA, String[] allergies,
                         String[] medicalHistory, LocalDate birthDate, String bloodType) {
        if (recordId == null || recordId.isBlank())
            throw new IllegalArgumentException("Record ID required");
        if (patientDNA == null || patientDNA.isBlank())
            throw new IllegalArgumentException("DNA sequence required");
        if (birthDate == null || birthDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Invalid birthdate");
        if (bloodType == null || bloodType.isBlank())
            throw new IllegalArgumentException("Blood type required");

        this.recordId = recordId;
        this.patientDNA = patientDNA;
        this.allergies = (allergies != null) ? allergies.clone() : new String[0];
        this.medicalHistory = (medicalHistory != null) ? medicalHistory.clone() : new String[0];
        this.birthDate = birthDate;
        this.bloodType = bloodType;
    }

    public String getRecordId() { return recordId; }
    public String getPatientDNA() { return patientDNA; }
    public String[] getAllergies() { return allergies.clone(); }
    public String[] getMedicalHistory() { return medicalHistory.clone(); }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBloodType() { return bloodType; }

    public final boolean isAllergicTo(String substance) {
        for (String allergy : allergies) {
            if (allergy.equalsIgnoreCase(substance)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "recordId='" + recordId + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", birthDate=" + birthDate +
                ", allergies=" + Arrays.toString(allergies) +
                ", medicalHistory entries=" + medicalHistory.length +
                '}';
    }

    @Override
    public int hashCode() { return Objects.hash(recordId, patientDNA); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MedicalRecord)) return false;
        MedicalRecord m = (MedicalRecord) o;
        return recordId.equals(m.recordId) && patientDNA.equals(m.patientDNA);
    }
}

// ===============================
// b. Patient Class
// ===============================
class Patient {
    private final String patientId;
    private final MedicalRecord medicalRecord;

    private String currentName;
    private String emergencyContact;
    private String insuranceInfo;

    private int roomNumber;
    private String attendingPhysician;

    // Constructor chaining
    // Emergency admission
    public Patient(String name) {
        this(UUID.randomUUID().toString(), null, name, "Unknown", "Pending", -1, "Unassigned");
    }

    // Standard admission
    public Patient(String id, MedicalRecord record, String name, String contact,
                   String insurance, int room, String physician) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Patient ID required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Patient name required");
        this.patientId = id;
        this.medicalRecord = record;
        this.currentName = name;
        this.emergencyContact = contact;
        this.insuranceInfo = insurance;
        this.roomNumber = room;
        this.attendingPhysician = physician;
    }

    // Transfer admission
    public Patient(MedicalRecord record, String name) {
        this(UUID.randomUUID().toString(), record, name, "Unknown", "Imported", -1, "Unassigned");
    }

    // JavaBean getters/setters
    public String getPatientId() { return patientId; }
    public MedicalRecord getMedicalRecord() { return medicalRecord; }

    public String getCurrentName() { return currentName; }
    public void setCurrentName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Invalid name");
        this.currentName = name;
    }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String contact) {
        this.emergencyContact = contact;
    }

    public String getInsuranceInfo() { return insuranceInfo; }
    public void setInsuranceInfo(String insuranceInfo) { this.insuranceInfo = insuranceInfo; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int room) { this.roomNumber = room; }

    public String getAttendingPhysician() { return attendingPhysician; }
    public void setAttendingPhysician(String physician) { this.attendingPhysician = physician; }

    // Package-private
    String getBasicInfo() {
        return "PatientID=" + patientId + ", Name=" + currentName + ", Room=" + roomNumber;
    }

    // Public info
    public String getPublicInfo() {
        return "Patient: " + currentName + " (Room " + roomNumber + ")";
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + patientId + '\'' +
                ", name='" + currentName + '\'' +
                ", room=" + roomNumber +
                ", attendingPhysician='" + attendingPhysician + '\'' +
                ", auditTime=" + LocalDate.now() +
                '}';
    }
}

// ===============================
// d. Medical Staff Classes
// ===============================
class Doctor {
    private final String licenseNumber;
    private final String specialty;
    private final Set<String> certifications;

    public Doctor(String licenseNumber, String specialty, Set<String> certifications) {
        this.licenseNumber = licenseNumber;
        this.specialty = specialty;
        this.certifications = new HashSet<>(certifications);
    }

    public String getLicenseNumber() { return licenseNumber; }
    public String getSpecialty() { return specialty; }
    public Set<String> getCertifications() { return new HashSet<>(certifications); }

    @Override
    public String toString() { return "Doctor[license=" + licenseNumber + ", specialty=" + specialty + "]"; }
}

class Nurse {
    private final String nurseId;
    private final String shift;
    private final List<String> qualifications;

    public Nurse(String id, String shift, List<String> qualifications) {
        this.nurseId = id;
        this.shift = shift;
        this.qualifications = new ArrayList<>(qualifications);
    }

    public String getNurseId() { return nurseId; }
    public String getShift() { return shift; }
    public List<String> getQualifications() { return new ArrayList<>(qualifications); }

    @Override
    public String toString() { return "Nurse[id=" + nurseId + ", shift=" + shift + "]"; }
}

class Administrator {
    private final String adminId;
    private final List<String> accessPermissions;

    public Administrator(String id, List<String> permissions) {
        this.adminId = id;
        this.accessPermissions = new ArrayList<>(permissions);
    }

    public String getAdminId() { return adminId; }
    public List<String> getAccessPermissions() { return new ArrayList<>(accessPermissions); }

    @Override
    public String toString() { return "Admin[id=" + adminId + "]"; }
}

// ===============================
// e. HospitalSystem with Access Control
// ===============================
class HospitalSystem {
    private final Map<String, Object> patientRegistry = new HashMap<>();

    static final String PRIVACY_POLICY = "HIPAA-STRICT";
    static final String HOSPITAL_RULES = "Patients' data classified";

    public boolean admitPatient(Object patient, Object staff) {
        if (!(patient instanceof Patient)) return false;
        if (!validateStaffAccess(staff, patient)) return false;
        Patient p = (Patient) patient;
        patientRegistry.put(p.getPatientId(), patient);
        return true;
    }

    private boolean validateStaffAccess(Object staff, Object patient) {
        if (staff instanceof Doctor) return true; // Full access
        if (staff instanceof Nurse) return true; // Limited access
        if (staff instanceof Administrator) return true; // Admin controls
        return false;
    }

    // Package-private internal op
    void internalAudit() {
        System.out.println("Audit: " + patientRegistry.keySet());
    }
}

// ===============================
// f. Demo Main
// ===============================
public class HospitalDemo {
    public static void main(String[] args) {
        // Create immutable medical record
        MedicalRecord record = new MedicalRecord("R001", "DNA-XYZ",
                new String[]{"Peanuts"}, new String[]{"Asthma"},
                LocalDate.of(1990, 5, 15), "O+");

        // Patient admission
        Patient p1 = new Patient(record, "John Doe");

        // Staff
        Doctor d1 = new Doctor("LIC123", "Cardiology", Set.of("BoardCertified"));
        Nurse n1 = new Nurse("NUR456", "Night", List.of("ICU Certified"));
        Administrator a1 = new Administrator("ADM789", List.of("AllAccess"));

        // Hospital system
        HospitalSystem system = new HospitalSystem();
        System.out.println("Admit patient by doctor: " + system.admitPatient(p1, d1));
        System.out.println("Public info: " + p1.getPublicInfo());
        System.out.println("MedicalRecord Allergic to Peanuts? " + record.isAllergicTo("Peanuts"));

        // Internal audit
        system.internalAudit();
    }
}
