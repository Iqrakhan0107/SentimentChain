@RestController
@RequestMapping("/api")
public class BlockchainController {

    @Autowired
    private SentimentDAO sentimentDAO;

    @GetMapping("/blockchain")
    public List<Map<String, Object>> getBlockchain() {
        return sentimentDAO.getBlockchain();
    }
}
