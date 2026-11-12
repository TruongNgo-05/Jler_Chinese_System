export function cartChanged() {
  window.dispatchEvent(new Event("cart-updated"));
}
