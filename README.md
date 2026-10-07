# Payriff Java SDK

Java client for the Payriff merchant API: orders, direct (host-to-host) card payments, saved cards,
transactions, payouts and invoices.

- Java 11 or newer
- One runtime dependency (Jackson)
- Talks to `https://api.payriff.com`

## Installation

> The artifact is not yet published to Maven Central. Until then, use JitPack:
>
> ```groovy
> repositories {
>     mavenCentral()
>     maven { url 'https://jitpack.io' }
> }
>
> dependencies {
>     implementation 'com.github.PayRiff:payriff-java:v0.1.2'
> }
> ```

Gradle:

```groovy
implementation 'com.payriff:payriff-java:0.1.2'
```

Maven:

```xml
<dependency>
  <groupId>com.payriff</groupId>
  <artifactId>payriff-java</artifactId>
  <version>0.1.2</version>
</dependency>
```

## Quick start

```java
PayriffClient payriff = PayriffClient.builder()
        .appKey(System.getenv("PAYRIFF_APP_KEY"))
        .build();

CreateOrderResponse order = payriff.orders().create(CreateOrderRequest.builder()
        .amount(new BigDecimal("10.00"))
        .description("Order #1001")
        .callbackUrl("https://shop.example/payriff/callback")
        .requestRrn("order-1001")
        .build());

// Send the customer to the hosted payment page
String paymentUrl = order.getPaymentUrl();
```

Create one `PayriffClient` and reuse it. It is immutable and thread-safe.

## Configuration

| Builder method | Required | Default | Notes |
|---|---|---|---|
| `appKey(String)` | yes | — | Application key from the Payriff dashboard. |
| `merchantId(String)` | for payouts and invoices | — | Merchant ID (e.g. `ES1000000`). |
| `connectTimeout(Duration)` | no | 10 s | |
| `requestTimeout(Duration)` | no | 60 s | Bank operations can take tens of seconds. |
| `cardEncryptionKey(String)` | no | built-in Payriff key | Only if Payriff rotates the card-encryption key. Base64 or PEM. |

Keep the app key in an environment variable or secret store. Never commit it.

## Orders

```java
OrdersApi orders = payriff.orders();

CreateOrderResponse created = orders.create(CreateOrderRequest.builder()
        .amount(new BigDecimal("25.00"))
        .currency(Currency.AZN)               // default AZN
        .operation(Operation.PRE_AUTH)        // default PURCHASE
        .language(Language.AZ)
        .description("Booking #77")
        .callbackUrl("https://shop.example/payriff/callback")
        .metadata("bookingId", "77")
        .build());

OrderInfo info = orders.get(created.getOrderId());
OrderInfo byRef = orders.getByRequestRrn("order-1001");   // the requestRrn you sent on create

orders.complete(CompleteRequest.builder(created.getOrderId()).amount(new BigDecimal("25.00")).build());
orders.refund(RefundRequest.builder(created.getOrderId())
        .amount(new BigDecimal("5.00"))
        .refundReason("Partial return")
        .build());
orders.expire(created.getOrderId());                         // cancel an unpaid order

byte[] receiptPdf = orders.downloadReceipt(created.getOrderId());
```

`OrderInfo.getPaymentStatus()` returns `APPROVED`, `DECLINED`, `PREAUTH_APPROVED`, `REFUNDED` and so on.
If Payriff adds a status that this SDK version does not know, it returns `PaymentStatus.UNKNOWN`
instead of failing.

## Direct payments (host-to-host)

You collect the card details yourself, and the SDK encrypts them before sending
(AES-256-GCM + RSA-OAEP). Raw card data never leaves your server in clear text, but your
systems still handle card data, so PCI DSS requirements apply to you.

```java
DirectPayResponse result = payriff.payments().directPay(DirectPayRequest.builder()
        .amount(new BigDecimal("1.00"))
        .description("Order #1002")
        .callbackUrl("https://shop.example/payriff/callback")
        .requestRrn("order-1002")
        .card(CardData.builder()
                .pan("4169 7413 3015 1979")
                .cardHolder("JOHN DOE")
                .expiryMonth("11")
                .expiryYear("27")
                .cvv("123")
                .build())
        .build());

if (result.isRedirect()) {
    // 3-D Secure: send the customer's browser to result.getRedirectUrl().
    // The final status arrives via your callback or orders().get(result.getOrderId()).
}
```

`CardData` strips spaces and dashes, pads the month (`1` → `01`), expands a 2-digit year (`27` → `2027`)
and rejects malformed values before any network call. Its `toString()` masks the PAN and omits the CVV.

### Charging a saved card

```java
AutoPayResponse charge = payriff.payments().autoPay(AutoPayRequest.builder()
        .cardUuid(savedCardUuid)
        .amount(new BigDecimal("9.99"))
        .description("Monthly subscription")
        .requestRrn("sub-2026-10")
        .build());
```

## Saved cards

```java
CardSaveResponse session = payriff.cards().save(CardSaveRequest.builder()
        .customerRef("customer-42")
        .callbackUrl("https://shop.example/payriff/card-saved")
        .idempotencyKey(UUID.randomUUID().toString())
        .build());
// Redirect the customer to session.getPaymentUrl() to verify the card.

CardSaveDetails details = payriff.cards().getSave(session.getCardSaveId());
if (details.getStatus() == CardSaveStatus.VERIFIED) {
    String cardUuid = details.getCardUuid();       // store it; use with autoPay
}

List<SavedCard> cards = payriff.cards().list("customer-42");
payriff.cards().delete(cards.get(0).getCardUuid());
```

## Transactions

```java
Page<Transaction> page = payriff.transactions().list(TransactionFilter.builder()
        .status(PaymentStatus.APPROVED)
        .from(LocalDate.of(2026, 9, 1))
        .to(LocalDate.of(2026, 9, 30))
        .page(0)
        .size(20)                                  // server maximum is 20
        .build());

page.getContent().forEach(tx -> System.out.println(tx.getOrderId() + " " + tx.getAmount()));
```

## Payouts

Payouts require `merchantId` on the client.

```java
PayriffClient payriff = PayriffClient.builder()
        .appKey(System.getenv("PAYRIFF_APP_KEY"))
        .merchantId(System.getenv("PAYRIFF_MERCHANT_ID"))
        .build();

String maskedName = payriff.payouts().checkCardholder("4169741330151979");  // e.g. "J*** D**"

PayoutResult payout = payriff.payouts().create(PayoutRequest.builder()
        .transferAmount(new BigDecimal("50.00"))   // minimum 1
        .description("Refund for order #1001")
        .fullName("JOHN DOE")
        .finCode("1AB2C3D")
        .cardPan("4169741330151979")
        .requestRrn("payout-1001")
        .idempotencyKey("payout-1001")
        .build());

PayoutStatus status = payriff.payouts().getByRequestRrn("payout-1001");
Page<PayoutSummary> history = payriff.payouts().list(PayoutFilter.builder().status(TransferState.SUCCESS).build());
byte[] receipt = payriff.payouts().downloadReceipt("payout-1001");
```

## Invoices

Invoices require `merchantId` on the client.

```java
Invoice invoice = payriff.invoices().create(InvoiceCreateRequest.builder()
        .amount(new BigDecimal("15.00"))
        .fullName("JOHN DOE")
        .phoneNumber("+994501234567")
        .description("Consultation")
        .expireDate(LocalDateTime.now().plusDays(7))
        .sendSms(true)
        .build());

String link = invoice.getPaymentUrl();             // share with the customer
InvoiceDetails details = payriff.invoices().get(invoice.getInvoiceUuid());
```

## Callbacks

When an order changes state, Payriff POSTs JSON to the `callbackUrl` you set on the order.

```java
@PostMapping("/payriff/callback")
public ResponseEntity<Void> callback(@RequestBody String body) {
    OrderInfo notified = Webhooks.parseOrderCallback(body);

    // Callbacks are not signed: confirm the state with Payriff before fulfilling.
    OrderInfo confirmed = payriff.orders().get(notified.getOrderId());
    if (confirmed.getPaymentStatus() == PaymentStatus.APPROVED) {
        // fulfil the order (make this idempotent: the same callback can arrive more than once)
    }
    return ResponseEntity.ok().build();
}
```

## Errors

Every SDK error extends `PayriffException`, which carries `getHttpStatus()`, `getCode()` (Payriff
result code) and `getResponseId()` (quote it when contacting support).

| Exception | When |
|---|---|
| `AuthenticationException` | App key rejected (`14010`, `14013`, `14014`, `14015`) |
| `ValidationException` | Invalid request (`15400` or HTTP 400) |
| `RequestRejectedException` | Business refusal (`01000`), e.g. application under review |
| `InsufficientBalanceException` | Not enough wallet balance for a payout (`01200`) |
| `PayoutLimitException` | Payout limit reached (`01300`, `01400`, `01500`) |
| `ApiException` | Any other failure reported by Payriff |
| `PayriffConnectionException` | No response: network error or timeout |

Payriff can report a failure with HTTP 200. The SDK checks the result code in the body, so you
only need to catch exceptions.

```java
try {
    payriff.orders().refund(RefundRequest.builder(orderId).build());
} catch (ValidationException e) {
    log.warn("Refund rejected: {} ({})", e.getMessage(), e.getCode());
} catch (PayriffConnectionException e) {
    // Outcome unknown: check orders().get(orderId) before retrying
} catch (PayriffException e) {
    log.error("Payriff error {} responseId={}", e.getCode(), e.getResponseId(), e);
}
```

### Retries

The SDK never retries on its own, because payment calls are not safe to repeat blindly. After a
`PayriffConnectionException`, look the operation up first (`orders().getByRequestRrn(...)`,
`payouts().getByRequestRrn(...)`) and retry only if it does not exist. Set `requestRrn` /
`idempotencyKey` on every request so that this lookup is possible.

## Building from source

```bash
./gradlew build
```

Gradle itself needs JDK 17 or newer; the library is compiled for Java 11. To run the tests on
another installed JDK, for example Java 11:

```bash
./gradlew test -PtestJavaVersion=11
```

## License

MIT
