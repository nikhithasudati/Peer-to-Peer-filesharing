
# Peer-to-Peer File Sharing

**Team Members:**
- Siva Praneeth Reddy Papineni
- Nikhitha Sudati
- Manoj Jampana

## Instructions to Run

1. Log into the CISE Linux server using SSH from the storm server.
2. Navigate to the project directory:
    ```
    cd CnSemproj/CNProject/P2Pfilesharing/src
    ```
3. Use SSH to access the corresponding Linux machines for running each peer:
   - Peer 1001:
     ```
     ssh s.papineni@lin114-00.cise.ufl.edu
     java bitTorrents/peer_1001/PeerProcess1001 1001
     ```
   - Peer 1002:
     ```
     ssh s.papineni@lin114-01.cise.ufl.edu
     java bitTorrents/peer_1002/PeerProcess1002 1002
     ```
   - Peer 1003:
     ```
     ssh s.papineni@lin114-02.cise.ufl.edu
     java bitTorrents/peer_1003/PeerProcess1003 1003
     ```
   - Peer 1004:
     ```
     ssh s.papineni@lin114-03.cise.ufl.edu
     java bitTorrents/peer_1004/PeerProcess1004 1004
     ```
   - Peer 1005:
     ```
     ssh s.papineni@lin114-04.cise.ufl.edu
     java bitTorrents/peer_1005/PeerProcess1005 1005
     ```

## Description of the Protocol

- The `Common.cfg` file contains details about the file size for peer-to-peer transfer, the piece size (size of individual file parts), and the number of preferred neighbors for a peer to choke/unchoke its neighbors.

- `Peerinfo.cfg` lists port numbers and corresponding UFL Linux machines for starting the peers.

- The demonstration involves 5 peers: `peer1001`, `peer1002`, `peer1003`, `peer1004`, and `peer1005`. `peer1001` initially possesses the entire file.

- The protocol starts with peers connecting and exchanging HANDSHAKE messages upon successful connection.

- BITFIELD messages are exchanged to specify the data indices of each peer. If a peer (e.g., `peer1002`) needs data from another peer (e.g., `peer1003`), it sends an INTERESTED message. If `peer1002` has the data, it sends a NOT INTERESTED message to `peer1003`.

- When a peer receives a BITFIELD or HAVE message, it sends a REQUEST message with the specific PIECE number it wants from the corresponding peer.

- Each peer selects its preferred k neighbors and changes them at regular UNCHOKE intervals. Peers transmit data between unchoking intervals, and the remaining peers are choked (CHOKE). Among the choked peers, one is optimistically unchoked, and messages are exchanged with that peer.

- When all peers have received the complete file, the connection is terminated, and the execution stops.

Feel free to adjust and expand this README as needed to provide more details or clarify any specific instructions or requirements for your project.
