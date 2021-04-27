package pro.belbix.ethparser.entity.a_layer;

import static pro.belbix.ethparser.service.AbiProviderService.BSC_NETWORK;
import static pro.belbix.ethparser.service.AbiProviderService.ETH_NETWORK;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pro.belbix.ethparser.dto.DtoI;
import pro.belbix.ethparser.entity.b_layer.ContractEventEntity;

//@NamedEntityGraph(
//    name = "block-graph.all",
//    attributeNodes = {
//        @NamedAttributeNode("hash"),
//        @NamedAttributeNode("parentHash"),
//        @NamedAttributeNode("transactionsRoot"),
//        @NamedAttributeNode("stateRoot"),
//        @NamedAttributeNode("receiptsRoot"),
//        @NamedAttributeNode("miner"),
//        @NamedAttributeNode("mixHash"),
//        @NamedAttributeNode(value = "transactions", subgraph = "tx.all"),
//    },
//    subgraphs = {
//        @NamedSubgraph(
//            name = "tx.all",
//            attributeNodes = {
//                @NamedAttributeNode("hash"),
//                @NamedAttributeNode("blockHash"),
//                @NamedAttributeNode("fromAddress"),
//                @NamedAttributeNode("toAddress"),
//                @NamedAttributeNode("r"),
//                @NamedAttributeNode("s"),
//            }
//        ),
//    }
//)
@Entity
@Table(name = "a_eth_block", indexes = {
    @Index(name = "idx_eth_block_hash", columnList = "hash"),
    @Index(name = "idx_eth_block_timestamp", columnList = "timestamp")
})
@Data
@JsonInclude(Include.NON_NULL)
public class EthBlockEntity implements DtoI {

    @Id
    private long number;
    private String nonce;
    private String author;
    private String difficulty;
    private String totalDifficulty;
    @Column(columnDefinition = "TEXT")
    private String extraData;
    private long size;
    private long gasLimit;
    private long gasUsed;
    private long timestamp;
    private int network;

    @ManyToOne
    @JoinColumn(name = "hash", referencedColumnName = "idx", unique = true)
    private EthHashEntity hash;

    @ManyToOne
    @JoinColumn(name = "parent_hash", referencedColumnName = "idx")
    private EthHashEntity parentHash;

    @ManyToOne
    @JoinColumn(name = "miner", referencedColumnName = "idx")
    private EthAddressEntity miner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "blockNumber",
        fetch = FetchType.EAGER, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<EthTxEntity> transactions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "block",
        fetch = FetchType.LAZY, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ContractEventEntity> contractEvents;

    public String network() {
        switch (network) {
            case 0:
                return ETH_NETWORK;
            case 1:
                return BSC_NETWORK;
            default:
                throw new IllegalStateException("Unknown network " + network);
        }
    }

    public void defineNetwork(String network) {
        switch (network) {
            case ETH_NETWORK:
                this.network = 0;
                break;
            case BSC_NETWORK:
                this.network = 1;
                break;
            default:
                throw new IllegalStateException("Unknown network " + network);
        }
    }
}