package vstr.service.transmisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vstr.model.Transmision;
import vstr.repository.TransmisionRepository;

import java.util.UUID;

@Service
public class TransmisionService {

    @Autowired
    private TransmisionRepository transmisionRepository;

    public Transmision findTransmisionByStreamer(String username) {
        return transmisionRepository.findByStreamerUsername(username); // Asegúrate de implementar este método en tu repositorio
    }

    public Transmision save(Transmision transmision) {
        return transmisionRepository.save(transmision);
    }

}
