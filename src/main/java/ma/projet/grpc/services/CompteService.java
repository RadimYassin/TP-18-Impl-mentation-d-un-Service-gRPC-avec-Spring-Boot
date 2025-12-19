package ma.projet.grpc.services;

import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.repositories.CompteRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompteService {
    private final CompteRepository compteRepository;

    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }

    public Compte findCompteById(String id) {
        return compteRepository.findById(id).orElse(null);
    }

    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    public int countComptes() {
        return (int) compteRepository.count();
    }

    public float sumSoldes() {
        return compteRepository.findAll().stream()
                .map(Compte::getSolde)
                .reduce(0f, Float::sum);
    }
}
