# expect: outer

fn main() {
  var outer = "outer"
  fn() {
    fn() {
      fn() {
        fn() {
          fn() {
            outer
          }()
        }()
      }()
    }()
  }()
}
