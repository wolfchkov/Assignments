/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wolf.assignment1;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A book includes all securities that the institution regularly buys and sells
 * on the stock market. A bid is the price that an institution is willing to buy
 * and an offer is the price that an institution is willing to sell.
 *
 * We want you to implement a Mini-Book class in Java. The following are the
 * requirements: - Mini-Book receives quotes as input. - A quote is a 5-tuple
 * string containing: QuoteID/{B|O}/{N|U|D}/Price/Volume QuoteID : alphanumeric
 * string which uniquely identifies a quote. B/O: whether the quote is for Bid
 * or Offer side. N/U/D: whether this is a new quote or an update or delete for
 * a previously received quote. A quote update can update either volume or price
 * or both. A delete for a quote id of “0” will delete the entire book i.e.
 * 0/B/D/0/0 will delete the entire bid book and 0/O/D/0/0 will delete the
 * entire offer book. Price: price associated with this quote. Volume: volume
 * associated with this quote. - Mini-Book then sorts the offers and the bids
 * from the quotes that it receives. - For offer, the best price is the lowest
 * price and for bid, the best price is the highest price. - The best price is
 * the first price that is displayed. - If two quotes have the same price, they
 * are then sorted by volume (from high to low). - If two quotes have the same
 * price and volume, they are then ranked by time - most recent to least recent.
 * - There should be a function for dumping the contents of the book. - Optimize
 * the code for speed and maintenance (simplicity is a good virtue)
 *
 * @author Andrey
 */
public class MiniBook {

    private final static Logger log = Logger.getLogger(MiniBook.class.getName());

    private final static int ID_INDEX = 0;
    private final static int TYPE_INDEX = 1;
    private final static int OPERATION_INDEX = 2;
    private final static int PRICE_INDEX = 3;
    private final static int VOLUME_INDEX = 4;

    private final static int VALUES = 5;

    private final Map<String, Quote> offers;
    private final Map<String, Quote> bids;

    public MiniBook() {
        this.offers = new HashMap<>();
        this.bids = new HashMap<>();
    }

    /**
     * Parse a list of quote string.
     * @param qoutesTuple 
     */
    public void readQuotes(List<String> qoutesTuple) {
        int rank = 0;

        //parse the qoutes and put it on offers or bids map depending on they type and opertaion 
        for (String qouteTuple : qoutesTuple) {
            String[] qouteValues = qouteTuple.split("/");

            if (qouteValues.length != VALUES) {
                log.log(Level.WARNING, "Incorrect qoute tuple format {0}. Skip! ", qouteTuple);
                continue;
            }

            String quoteType = qouteValues[TYPE_INDEX];

            switch (quoteType) {
                
                //handle bid quote 
                case "B":
                    handleQuote(rank, qouteValues, bids);
                    break;

                 //handle offer quote 
                case "O":
                    handleQuote(rank, qouteValues, offers);
                    break;

                default:
                    log.log(Level.WARNING, "Illegal qoute type {0}. Skip! ", quoteType);
            }

            rank++;
        }
    }

    private void handleQuote(int rank, String[] qouteValues, Map<String, Quote> qMap) {
        String quoteId = qouteValues[ID_INDEX];
        String quoteOp = qouteValues[OPERATION_INDEX];
        
        //depending on qoute opertaion add it, update or delete  
        switch (quoteOp) {

            case "N":
                if (qMap.containsKey(quoteId)) {
                    log.log(Level.WARNING, "The quote with id = {0} allready added. Skip! ", quoteId);
                    break;
                }
                Quote quote = new Quote(qouteValues[0], rank,
                        new BigDecimal(qouteValues[PRICE_INDEX]), Integer.parseInt(qouteValues[VOLUME_INDEX]));
                qMap.put(quoteId, quote);
                break;

            case "U":
                quote = qMap.get(quoteId);
                if (Objects.nonNull(quote)) {
                    quote.updatePrice(new BigDecimal(qouteValues[3]));
                    quote.updateVolume(Integer.parseInt(qouteValues[4]));
                } else {
                    log.log(Level.WARNING, "The quote with id = {0} not found for update. Skip! ", quoteId);
                }
                break;
            case "D":
                qMap.remove(quoteId);
                break;

            default:
                log.log(Level.WARNING, "Incorrect qoute opertaion value {0}. Skip! ", quoteOp);
        }

    }

    /**
     * Compare two bids, on firts place bid with higher price
     * @param q1 first 
     * @param q2 second
     * @return 
     */
    private int compareBid(Quote q1, Quote q2) {
        int cmp = q2.getPrice().compareTo(q1.getPrice());

        if (cmp == 0) {
            return compareAmmountAndRank(q1, q2);
        }

        return cmp;
    }

    /**
     * Compare two offers, on firts place offer with lower price
     * @param q1 first 
     * @param q2 second
     * @return 
     */    
    private int compareOffer(Quote q1, Quote q2) {
        int cmp = q1.getPrice().compareTo(q2.getPrice());

        if (cmp == 0) {
            return compareAmmountAndRank(q1, q2);
        }

        return cmp;
    }

    private int compareAmmountAndRank(Quote q1, Quote q2) {
        int cmp = q2.getVolume() - q1.getVolume();

        if (cmp == 0) {
            return q1.getRank() - q2.getRank();
        }

        return cmp;
    }

    /**
     * Dump book content to PrintStream
     * @param print PrintStream to dump
     */
    public void dumpContent(final PrintStream print) {

        print.println("OFFER");
        offers.values()
                .stream()
                .sorted(this::compareOffer)
                .forEach(print::println);

        print.println();

        print.println("BID");
        bids.values()
                .stream()
                .sorted(this::compareBid)
                .forEach(print::println);

    }

    public static void main(String[] args) {

        
        if (args.length != 1) {
            log.log(Level.SEVERE, "Expected argument of input file!");
        } 
        
        Path inputFile = Paths.get(args[0]);
        List<String> qoutesTuple;
        try {
            qoutesTuple = Files.lines(inputFile)
                    .collect(Collectors.toList());

            MiniBook miniBook = new MiniBook();
            miniBook.readQuotes(qoutesTuple);

            miniBook.dumpContent(System.out);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "IO error", ex);
        }

    }

}
